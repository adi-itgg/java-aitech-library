package io.aitech.vertx.http.processor.impl;

import io.aitech.vertx.http.annotation.route.Name;
import io.aitech.vertx.http.model.RouteOptions;
import io.aitech.vertx.http.processor.RouteAnnotationProcessor;
import lombok.val;

import java.lang.reflect.Method;

public class NameRouteAnnotationProcessor implements RouteAnnotationProcessor {

  @Override
  public boolean isSupported(Object context, Class<?> typeClass, Method method) {
    return method != null && method.isAnnotationPresent(Name.class);
  }

  @Override
  public void process(Object context, Class<?> typeClass, Method method, RouteOptions options) {
    val name = typeClass.getAnnotation(Name.class);
    if (name != null) {
      options.name(name.value());
    }

    if (method == null) {
      return;
    }
    val nameMethod = method.getAnnotation(Name.class);
    if (nameMethod != null) {
      options.name(nameMethod.value());
    }
  }

}
