package io.aitech.vertx.http.processor.impl;

import io.aitech.vertx.http.annotation.route.ExcludeMethodPath;
import io.aitech.vertx.http.model.RouteOptions;
import io.aitech.vertx.http.processor.RouteAnnotationProcessor;
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
