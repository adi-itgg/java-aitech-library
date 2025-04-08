package io.aitech.vertx.db.orm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Entity {

  /**
   * (Optional) The entity name. Defaults to the unqualified
   * name of the entity class. This name is used to refer to the
   * entity in queries. The name must not be a reserved literal
   * in the Jakarta Persistence query language.
   */
  String name() default "";

}
