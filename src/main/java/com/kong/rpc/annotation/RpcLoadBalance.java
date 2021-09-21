package com.kong.rpc.annotation;

import java.lang.annotation.*;

/**
 * 负载均衡
 * @author 10285
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcLoadBalance {
    String value() default "";
}
