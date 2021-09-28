package com.kong.rpc.io.netty;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kong.rpc.io.handler.RequestHandler;
import com.kong.rpc.registry.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyRpcServer extends RpcServer {
    private static Logger LOGGER = LoggerFactory.getLogger(NettyRpcServer.class);

    private Channel channel;
    public NettyRpcServer(int port, String protocol, RequestHandler requestHandler) {
        super(port, protocol, requestHandler);
    }

    private static final ExecutorService pool = new ThreadPoolExecutor(4,8,200, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactoryBuilder().setNameFormat("rpcServer-%d").build()
            );

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,100)
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new ChannelRequestHandler());
                }
            });
            ChannelFuture future = b.bind(port).sync();
            LOGGER.debug("RpcServer started successfully");
            channel = future.channel();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("start netty server failed,msg:{}",e.getMessage());
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        this.channel.close();
    }
    private class ChannelRequestHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            LOGGER.debug("Channel active :{}", ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            pool.submit(()->{
                try {
                    LOGGER.debug("the server receives message :{}", msg);
                    ByteBuf byteBuf = (ByteBuf) msg;
                    // 消息写入reqData
                    byte[] reqData = new byte[byteBuf.readableBytes()];
                    byteBuf.readBytes(reqData);
                    // 手动回收
                    ReferenceCountUtil.release(byteBuf);
                    byte[] respData = requestHandler.handleRequest(reqData);
                    ByteBuf respBuf = Unpooled.buffer(respData.length);
                    respBuf.writeBytes(respData);
                    LOGGER.debug("Send response:{}", respBuf);
                    ctx.writeAndFlush(respBuf);
                }catch (Exception e){
                    LOGGER.error("server read exception",e);
                }
            });
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            // Close the connection when an exception is raised.
            cause.printStackTrace();
            LOGGER.error("Exception occurred:{}", cause.getMessage());
            ctx.close();
        }
    }

}
