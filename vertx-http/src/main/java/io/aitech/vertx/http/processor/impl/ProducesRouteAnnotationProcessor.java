package io.aitech.vertx.http.processor.impl;

import io.aitech.vertx.http.annotation.route.Produces;
import io.aitech.vertx.http.model.RouteOptions;
import io.aitech.vertx.http.processor.RouteAnnotationProcessor;
import lombok.val;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

public class ProducesRouteAnnotationProcessor implements RouteAnnotationProcessor {

  @Override
  public boolean isSupported(Object context, Class<?> typeClass, Method method) {
    return typeClass.isAnnotationPresent(Produces.class) || (method != null && method.isAnnotationPresent(Produces.class));
  }

  @SuppressWarnings("SuspiciousToArrayCall")
  @Override
  public void process(Object context, Class<?> typeClass, Method method, RouteOptions options) {
    val typeProduces = Arrays.stream(typeClass.getAnnotationsByType(Produces.class))
      .map(Produces::value).flatMap(Arrays::stream).toArray(String[]::new);
    val methodProduces = method == null ? Stream.empty() : Arrays.stream(method.getAnnotationsByType(Produces.class)).map(Produces::value).flatMap(Arrays::stream);
    val allProduces = Stream.concat(Arrays.stream(typeProduces), methodProduces).toArray(String[]::new);
    options.produces(allProduces);
  }

}
