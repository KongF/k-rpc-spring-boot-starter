package com.kong.rpc.common;


/**
 * @author 10285
 */

public enum KrpcStatusEnum {

    /**
     * Success
     */
    SUCCESS(200, "SUCCESS"),

    /**
     * Bad Request
     */
    BAD_REQUEST(400,"BAD REQUEST"),

    /**
     * Not Found
     */
    NOT_FOUND(404, "NOT FOUND"),

    /**
     * Error
     */
    ERROR(500, "INTERNAL SERVER ERROR");


    private long code;

    private String message;

    KrpcStatusEnum(long code, String message){
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
