package io.github.adiitgg.vertx.http.annotation.route;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Blocking {

  boolean ordered() default true;

}
