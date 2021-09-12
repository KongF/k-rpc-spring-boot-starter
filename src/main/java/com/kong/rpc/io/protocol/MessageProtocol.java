package com.kong.rpc.io.protocol;

import com.kong.rpc.common.KrpcRequest;
import com.kong.rpc.common.KrpcResponse;

/**
 * 消息协议:定义编组请求、解组请求、编组响应、解组响应的规范
 * @author 10285
 */
public interface MessageProtocol {

    /**
     * marshalling request
     * @param request
     * @return
     * @throws Exception
     */
    byte[] marshallingRequest(KrpcRequest request) throws Exception;

    /**
     * unmarshalling request
     * @param data
     * @return
     */
    KrpcRequest unmarshallingRequest(byte[] data) throws Exception;

    /**
     * marshalling response
     * @param response
     * @return
     * @throws Exception
     */
    byte[] marshallingResponse(KrpcResponse response) throws Exception;
    /**
     * 解组响应
     * @param data
     * @return
     * @throws Exception
     */
    KrpcResponse unmarshallingResponse(byte[] data) throws Exception;
}
