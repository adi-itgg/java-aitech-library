package io.github.adiitgg.vertx.http.processor.impl;

import io.github.adiitgg.vertx.http.annotation.route.Disabled;
import io.github.adiitgg.vertx.http.model.RouteOptions;
import io.github.adiitgg.vertx.http.processor.RouteAnnotationProcessor;
import lombok.val;

import java.lang.reflect.Method;

public class DisabledRouteAnnotationProcessor implements RouteAnnotationProcessor {

  @Override
  public boolean isSupported(Object context, Class<?> typeClass, Method method) {
    return typeClass.isAnnotationPresent(Disabled.class) || (method != null && method.isAnnotationPresent(Disabled.class));
  }

  @Override
  public void process(Object context, Class<?> typeClass, Method method, RouteOptions options) {
    val typeDisabled = typeClass.isAnnotationPresent(Disabled.class);
    val methodDisabled = method != null && method.isAnnotationPresent(Disabled.class);
    options.enable(!typeDisabled && !methodDisabled);
  }

}
