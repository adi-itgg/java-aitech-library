package io.github.adiitgg.vertx.http.processor.impl;

import io.github.adiitgg.vertx.http.annotation.route.Order;
import io.github.adiitgg.vertx.http.model.RouteOptions;
import io.github.adiitgg.vertx.http.processor.RouteAnnotationProcessor;
import lombok.val;

import java.lang.reflect.Method;

public class OrderRouteAnnotationProcessor implements RouteAnnotationProcessor {

  @Override
  public boolean isSupported(Object context, Class<?> typeClass, Method method) {
    return method != null && method.isAnnotationPresent(Order.class);
  }

  @Override
  public void process(Object context, Class<?> typeClass, Method method, RouteOptions options) {
    val order = method.getAnnotation(Order.class);
    options.order(order.value());
  }

}
