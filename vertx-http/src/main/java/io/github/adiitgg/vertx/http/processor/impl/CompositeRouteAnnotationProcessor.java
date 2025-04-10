package io.github.adiitgg.vertx.http.processor.impl;

import io.github.adiitgg.vertx.http.model.RouteOptions;
import io.github.adiitgg.vertx.http.processor.RouteAnnotationProcessor;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class CompositeRouteAnnotationProcessor implements RouteAnnotationProcessor {

  private final RouteAnnotationProcessor[] processors;

  @Override
  public boolean isSupported(Object context, Class<?> typeClass, Method method) {
    for (RouteAnnotationProcessor processor : processors) {
      if (processor.isSupported(context, typeClass, method)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void process(Object context, Class<?> typeClass, Method method, RouteOptions options) {
    for (RouteAnnotationProcessor processor : processors) {
      if (processor.isSupported(context, typeClass, method)) {
        processor.process(context, typeClass, method, options);
      }
    }
  }

}
