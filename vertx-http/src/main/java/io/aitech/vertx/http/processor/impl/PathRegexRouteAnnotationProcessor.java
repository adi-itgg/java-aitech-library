package io.aitech.vertx.http.processor.impl;

import io.aitech.vertx.http.annotation.route.PathRegex;
import io.aitech.vertx.http.model.RouteOptions;
import io.aitech.vertx.http.processor.RouteAnnotationProcessor;
import lombok.val;

import java.lang.reflect.Method;

public class PathRegexRouteAnnotationProcessor implements RouteAnnotationProcessor {

  @Override
  public boolean isSupported(Object context, Class<?> typeClass, Method method) {
    return method != null && method.isAnnotationPresent(PathRegex.class);
  }

  @Override
  public void process(Object context, Class<?> typeClass, Method method, RouteOptions options) {
    val pathRegex = method.getAnnotation(PathRegex.class).value();
    options.pathRegex(pathRegex);
  }

}
