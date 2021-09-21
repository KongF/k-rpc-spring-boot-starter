package com.kong.rpc.io.netty;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kong.rpc.common.KrpcRequest;
import com.kong.rpc.common.KrpcResponse;
import com.kong.rpc.io.NetClient;
import com.kong.rpc.io.protocol.MessageProtocol;
import com.kong.rpc.model.ServiceResourse;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.Map;
import java.util.concurrent.*;

public class NettyNetClient implements NetClient {

    private static ExecutorService threadPool = new ThreadPoolExecutor(4, 10, 200,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new ThreadFactoryBuilder()
            .setNameFormat("rpcClient-%d")
            .build());
    private EventLoopGroup loopGroup = new NioEventLoopGroup(4);

    public static Map<String,Object> connectedServerNodes = new ConcurrentHashMap<>();

    @Override
    public byte[] sendRequest(byte[] data, ServiceResourse serviceResourse) throws InterruptedException {
        return new byte[0];
    }

    @Override
    public KrpcResponse sendRequest(KrpcRequest request, ServiceResourse serviceResourse, MessageProtocol messageProtocol) {
        return null;
    }
}
