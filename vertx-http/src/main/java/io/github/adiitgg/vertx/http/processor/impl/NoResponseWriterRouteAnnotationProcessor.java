package io.github.adiitgg.vertx.http.processor.impl;

import io.github.adiitgg.vertx.http.annotation.route.NoResponseWriter;
import io.github.adiitgg.vertx.http.model.RouteOptions;
import io.github.adiitgg.vertx.http.processor.RouteAnnotationProcessor;
import lombok.val;

import java.lang.reflect.Method;

public class NoResponseWriterRouteAnnotationProcessor implements RouteAnnotationProcessor {

  @Override
  public boolean isSupported(Object context, Class<?> typeClass, Method method) {
    return typeClass.isAnnotationPresent(NoResponseWriter.class) || (method != null && method.isAnnotationPresent(NoResponseWriter.class));
  }

  @Override
  public void process(Object context, Class<?> typeClass, Method method, RouteOptions options) {
    val typeNoResponse = typeClass.isAnnotationPresent(NoResponseWriter.class);
    val methodNoResponse = method != null && method.isAnnotationPresent(NoResponseWriter.class);
    options.noResponseWriter(typeNoResponse || methodNoResponse);
  }

}
