package io.aitech.vertx.http.processor.impl;

import io.aitech.vertx.http.annotation.route.Blocking;
import io.aitech.vertx.http.model.RouteOptions;
import io.aitech.vertx.http.processor.RouteAnnotationProcessor;
import lombok.val;

import java.lang.reflect.Method;

public class BlockingRouteAnnotationProcessor implements RouteAnnotationProcessor {

  @Override
  public boolean isSupported(Object context, Class<?> typeClass, Method method) {
    return typeClass.isAnnotationPresent(Blocking.class) || (method != null && method.isAnnotationPresent(Blocking.class));
  }

  @Override
  public void process(Object context, Class<?> typeClass, Method method, RouteOptions options) {
    val typeBlocking = typeClass.getAnnotation(Blocking.class);
    val methodBlocking = method == null ? null : method.getAnnotation(Blocking.class);
    options.isBlocking(typeBlocking != null || methodBlocking != null);
    if (options.isBlocking()) {
      options.blockingOrdered(typeBlocking != null && typeBlocking.ordered() || methodBlocking != null && methodBlocking.ordered());
    }
  }

}
