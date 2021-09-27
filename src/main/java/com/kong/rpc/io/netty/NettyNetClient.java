package com.kong.rpc.io.netty;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kong.rpc.common.KrpcRequest;
import com.kong.rpc.common.KrpcResponse;
import com.kong.rpc.io.NetClient;
import com.kong.rpc.io.handler.SendHandler;
import com.kong.rpc.io.handler.SendHandlerV2;
import com.kong.rpc.io.protocol.MessageProtocol;
import com.kong.rpc.model.ServiceResourse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

public class NettyNetClient implements NetClient {
    private static Logger LOGGER = LoggerFactory.getLogger(NettyNetClient.class);
    private static ExecutorService threadPool = new ThreadPoolExecutor(4, 10, 200,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new ThreadFactoryBuilder()
            .setNameFormat("rpcClient-%d")
            .build());
    private EventLoopGroup loopGroup = new NioEventLoopGroup(4);

    public static Map<String,SendHandlerV2> connectedServerNodes = new ConcurrentHashMap<>();

    @Override
    public byte[] sendRequest(byte[] data, ServiceResourse serviceResourse) throws InterruptedException {
        String address = serviceResourse.getAddress();
        String[] addrInfo = address.split(":");
        final String serverAddress = addrInfo[0];
        final String serverPort = addrInfo[1];
        SendHandler sendHandler = new SendHandler(data);
        byte[] resData;
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception{
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(sendHandler);
                        }
                    });
            // client connection
            bootstrap.connect(serverAddress,Integer.parseInt(serverPort)).sync();
            resData = (byte[])sendHandler.respData();
            LOGGER.debug("SendRequest get reply:{}",resData);
        }finally {
            loopGroup.shutdownGracefully();
        }
        return resData;
    }

    @Override
    public KrpcResponse sendRequest(KrpcRequest request, ServiceResourse serviceResourse, MessageProtocol messageProtocol) {
        String address = serviceResourse.getAddress();
        synchronized (address){
            if(connectedServerNodes.containsKey(address)){
                SendHandlerV2 handlerV2 = connectedServerNodes.get(address);
                LOGGER.info("使用现有连接");
                return handlerV2.sendRequest(request);
            }
            String[] addInfo =address.split(":");
            final String serverAddress =addInfo[0];
            final String serverPort = addInfo[1];
            final SendHandlerV2 handlerV2 = new SendHandlerV2(messageProtocol,address);
            threadPool.submit(()->{
               Bootstrap bootstrap = new Bootstrap();
               bootstrap.group(loopGroup).channel(NioSocketChannel.class)
                       .option(ChannelOption.TCP_NODELAY,true)
                       .handler(new ChannelInitializer<SocketChannel>() {
                           @Override
                           protected void initChannel(SocketChannel socketChannel) throws Exception{
                               ChannelPipeline pipeline = socketChannel.pipeline();
                               pipeline.addLast(new FixedLengthFrameDecoder(20))
                                       .addLast(handlerV2);
                           }
                       });
                ChannelFuture channelFuture = bootstrap.connect(serverAddress,Integer.parseInt(serverPort));
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        connectedServerNodes.put(address,handlerV2);
                    }
                });
            });
            LOGGER.info("使用现有连接...");
            return handlerV2.sendRequest(request);
        }
    }
}
