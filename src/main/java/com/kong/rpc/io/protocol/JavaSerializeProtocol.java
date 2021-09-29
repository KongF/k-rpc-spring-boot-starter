package com.kong.rpc.io.protocol;

import com.kong.rpc.annotation.RpcMessageProtocol;
import com.kong.rpc.common.KrpcRequest;
import com.kong.rpc.common.KrpcResponse;
import com.kong.rpc.constants.KrpcConstant;

import java.io.*;

/**
 * @author k
 */
@RpcMessageProtocol(KrpcConstant.PROTOCOL_JAVA)
public class JavaSerializeProtocol implements MessageProtocol{
    private byte[] serialize(Object o) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream outOS = new ObjectOutputStream(os);
        outOS.writeObject(o);
        return  os.toByteArray();
    }
    private Object deSerialize(byte[] data) throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(data));
        return  is.readObject();
    }

    @Override
    public byte[] marshallingRequest(KrpcRequest request) throws Exception {
        return serialize(request);
    }

    @Override
    public KrpcRequest unmarshallingRequest(byte[] data) throws Exception {
        return (KrpcRequest) deSerialize(data);
    }

    @Override
    public byte[] marshallingResponse(KrpcResponse response) throws Exception {

        return serialize(response);
    }

    @Override
    public KrpcResponse unmarshallingResponse(byte[] data) throws Exception {
        return (KrpcResponse) deSerialize(data);
    }
}
