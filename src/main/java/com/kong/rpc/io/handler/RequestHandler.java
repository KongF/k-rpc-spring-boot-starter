package com.kong.rpc.io.handler;

import com.kong.rpc.common.KrpcRequest;
import com.kong.rpc.common.KrpcResponse;
import com.kong.rpc.common.KrpcStatusEnum;
import com.kong.rpc.io.protocol.MessageProtocol;
import com.kong.rpc.model.ServiceObject;
import com.kong.rpc.registry.Registry;

import java.lang.reflect.Method;

public class RequestHandler {
    private MessageProtocol protocol;
    private Registry registrer;

    public RequestHandler(MessageProtocol protocol, Registry registrer) {
        this.protocol = protocol;
        this.registrer = registrer;
    }

    public byte[] handleRequest(byte[] data) throws Exception {
        //解组消息
        KrpcRequest request = this.protocol.unmarshallingRequest(data);
        //获取服务
        ServiceObject serviceObject = registrer.getServiceObject(request.getServiceName());
        KrpcResponse response = null;
        if(serviceObject==null){
            response = new KrpcResponse(KrpcStatusEnum.NOT_FOUND);
        }else{
            try{
                Method method = serviceObject.getClazz().getMethod(request.getMethod(),request.getParameterTypes());
                Object returnValue= method.invoke(serviceObject.getObject(),request.getParameters());
                response = new KrpcResponse(KrpcStatusEnum.SUCCESS);
                response.setReturnValue(returnValue);
            }catch (Exception e){
                response = new KrpcResponse(KrpcStatusEnum.ERROR);
                response.setException(e);
            }
        }
        response.setRequestId(request.getRequestId());
        return this.protocol.marshallingResponse(response);
    }

    public MessageProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(MessageProtocol protocol) {
        this.protocol = protocol;
    }

    public Registry getRegistrer() {
        return registrer;
    }

    public void setRegistrer(Registry registrer) {
        this.registrer = registrer;
    }
}
