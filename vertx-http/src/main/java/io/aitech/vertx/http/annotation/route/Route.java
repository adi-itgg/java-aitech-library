package io.aitech.vertx.http.annotation.route;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {

  /**
   * rest path
   */
  String value() default "";

}
