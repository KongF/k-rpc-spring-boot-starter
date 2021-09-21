package com.kong.rpc.annotation;

import java.lang.annotation.*;

/**
 * 协议
 * @author 10285
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcMessageProtocol {
    String value() default "";
}
