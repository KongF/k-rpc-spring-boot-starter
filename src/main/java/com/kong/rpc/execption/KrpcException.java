package com.kong.rpc.execption;

/**
 * @author 10285
 */
public class KrpcException extends RuntimeException {

    public KrpcException(String message) {
        super(message);
    }
}
