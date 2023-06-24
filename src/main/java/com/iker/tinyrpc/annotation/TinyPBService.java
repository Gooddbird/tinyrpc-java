package com.iker.tinyrpc.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TinyPBService {
    String serviceName() default "";
}
