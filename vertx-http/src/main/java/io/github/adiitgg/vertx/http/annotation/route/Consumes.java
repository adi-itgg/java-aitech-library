package io.github.adiitgg.vertx.http.annotation.route;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Consumes {

  String[] value();

}
