package io.aitech.vertx.http.annotation.middleware;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Middleware {

  boolean beforeAuth() default false;

}
