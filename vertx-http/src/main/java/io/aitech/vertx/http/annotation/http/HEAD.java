package io.aitech.vertx.http.annotation.http;


import io.aitech.vertx.http.model.Method;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@API(method = Method.HEAD)
public @interface HEAD {

  String value() default "";

}
