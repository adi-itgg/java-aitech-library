package io.github.adiitgg.vertx.http.processor.impl;

import io.github.adiitgg.vertx.http.annotation.route.ExcludeMethodPath;
import io.github.adiitgg.vertx.http.model.RouteOptions;
import io.github.adiitgg.vertx.http.processor.RouteAnnotationProcessor;
import lombok.val;

import java.lang.reflect.Method;

public class ExcludeMethodPathRouteAnnotationProcessor implements RouteAnnotationProcessor {

  @Override
  public boolean isSupported(Object context, Class<?> typeClass, Method method) {
    return typeClass.isAnnotationPresent(ExcludeMethodPath.class) || (method != null && method.isAnnotationPresent(ExcludeMethodPath.class));
  }

  @Override
  public void process(Object context, Class<?> typeClass, Method method, RouteOptions options) {
    val typeNoMethodAsPath = typeClass.isAnnotationPresent(ExcludeMethodPath.class);
    val methodNoMethodAsPath = method != null && method.isAnnotationPresent(ExcludeMethodPath.class);
    options.methodAsPath(!typeNoMethodAsPath && !methodNoMethodAsPath);
  }

}
