package io.github.adiitgg.vertx.http.processor;

import io.github.adiitgg.vertx.http.model.RouteOptions;

import java.lang.reflect.Method;

public interface RouteAnnotationProcessor {

  boolean isSupported(Object context, Class<?> typeClass, Method method);

  void process(Object context, Class<?> typeClass, Method method, RouteOptions options);

}
