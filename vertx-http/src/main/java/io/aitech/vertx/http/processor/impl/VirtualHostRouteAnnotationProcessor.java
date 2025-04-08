package io.aitech.vertx.http.processor.impl;

import io.aitech.vertx.http.annotation.route.VirtualHost;
import io.aitech.vertx.http.model.RouteOptions;
import io.aitech.vertx.http.processor.RouteAnnotationProcessor;
import lombok.val;

import java.lang.reflect.Method;

public class VirtualHostRouteAnnotationProcessor implements RouteAnnotationProcessor {

  @Override
  public boolean isSupported(Object context, Class<?> typeClass, Method method) {
    return method != null && method.isAnnotationPresent(VirtualHost.class);
  }

  @Override
  public void process(Object context, Class<?> typeClass, Method method, RouteOptions options) {
    val virtualHost = method.getAnnotation(VirtualHost.class).value();
    options.virtualHost(virtualHost);
  }

}
