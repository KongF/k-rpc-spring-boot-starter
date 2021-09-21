package com.kong.rpc.serializer;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class SerializerUtil {
    /**
     * 序列化
     * @param source
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T source){
        Schema<T> schema = RuntimeSchema.getSchema((Class<T>)source.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        final byte[] reuslt;
        try{
            reuslt = ProtobufIOUtil.toByteArray(source,schema,buffer);
        }finally {
            buffer.clear();
        }
        return reuslt;
    }
    public static <T> T deserialize(byte[] source,Class<T> clazz){
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T t = schema.newMessage();
        ProtobufIOUtil.mergeFrom(source,t,schema);
        return t;
    }
}
