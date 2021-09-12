package com.kong.rpc.io;

import com.kong.rpc.common.KrpcRequest;
import com.kong.rpc.common.KrpcResponse;
import com.kong.rpc.io.protocol.MessageProtocol;
import com.kong.rpc.model.ServiceResourse;

/**
 * 客户端网络请求处理
 * @author 10285
 */
public interface NetClient {
    byte[] sendRequest(byte[] data, ServiceResourse serviceResourse) throws InterruptedException;
    KrpcResponse sendRequest(KrpcRequest request, ServiceResourse serviceResourse, MessageProtocol messageProtocol);
}
