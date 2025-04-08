package io.aitech.vertx.http.annotation.http;


import io.aitech.vertx.http.model.Method;

import java.lang.annotation.*;

@Documented
@Inherited
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface API {

  Method method() default Method.GET;

  String value() default "";

}
