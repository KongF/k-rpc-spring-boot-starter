package com.kong.rpc.io.protocol;

import com.kong.rpc.common.KrpcRequest;
import com.kong.rpc.common.KrpcResponse;
import com.kong.rpc.serializer.SerializerUtil;

public class ProtoBufProtocol implements MessageProtocol{
    @Override
    public byte[] marshallingRequest(KrpcRequest request) throws Exception {
        return SerializerUtil.serialize(request);
    }

    @Override
    public KrpcRequest unmarshallingRequest(byte[] data) throws Exception {
        return SerializerUtil.deserialize(data,KrpcRequest.class);
    }

    @Override
    public byte[] marshallingResponse(KrpcResponse response) throws Exception {
        return SerializerUtil.serialize(response);
    }

    @Override
    public KrpcResponse unmarshallingResponse(byte[] data) throws Exception {
        return SerializerUtil.deserialize(data,KrpcResponse.class);
    }
}
