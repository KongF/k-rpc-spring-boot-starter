package com.kong.rpc.annotation;

import java.lang.annotation.*;

/**
 * @author 10285
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectRpcService {
}
