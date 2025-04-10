package io.github.adiitgg.vertx.http.processor.impl;

import io.github.adiitgg.vertx.http.annotation.route.Consumes;
import io.github.adiitgg.vertx.http.model.RouteOptions;
import io.github.adiitgg.vertx.http.processor.RouteAnnotationProcessor;
import lombok.val;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

public class ConsumesRouteAnnotationProcessor implements RouteAnnotationProcessor {

  @Override
  public boolean isSupported(Object context, Class<?> typeClass, Method method) {
    return typeClass.isAnnotationPresent(Consumes.class) || (method != null && method.isAnnotationPresent(Consumes.class));
  }

  @SuppressWarnings("SuspiciousToArrayCall")
  @Override
  public void process(Object context, Class<?> typeClass, Method method, RouteOptions options) {
    val typeConsumes = Arrays.stream(typeClass.getAnnotationsByType(Consumes.class))
      .map(Consumes::value).flatMap(Arrays::stream).toArray(String[]::new);
    val methodConsumes = method == null ? Stream.empty() : Arrays.stream(method.getAnnotationsByType(Consumes.class)).map(Consumes::value).flatMap(Arrays::stream);
    val allConsumes = Stream.concat(Arrays.stream(typeConsumes), methodConsumes).toArray(String[]::new);
    options.consumes(allConsumes);
  }

}
