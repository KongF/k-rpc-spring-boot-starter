package com.kong.rpc.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装RPC响应
 * @author 10285
 */
public class KrpcResponse implements Serializable {
    private String requestId;
    private Map<String,String> header = new HashMap<>();
    private Object returnValue;
    private Exception exception;
    private KrpcStatusEnum statusEnum;

    public KrpcResponse(KrpcStatusEnum statusEnum){
        this.statusEnum = statusEnum;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public KrpcStatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(KrpcStatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }
}
