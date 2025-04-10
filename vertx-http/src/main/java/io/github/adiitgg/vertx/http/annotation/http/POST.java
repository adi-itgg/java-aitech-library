package io.github.adiitgg.vertx.http.annotation.http;




import io.github.adiitgg.vertx.http.model.Method;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@API(method = Method.POST)
public @interface POST {

  String value() default "";

}
