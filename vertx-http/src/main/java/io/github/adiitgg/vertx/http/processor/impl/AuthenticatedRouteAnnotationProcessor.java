package io.github.adiitgg.vertx.http.processor.impl;

import io.github.adiitgg.vertx.http.annotation.route.Authenticated;
import io.github.adiitgg.vertx.http.model.RouteOptions;
import io.github.adiitgg.vertx.http.processor.RouteAnnotationProcessor;
import lombok.val;

import java.lang.reflect.Method;

public class AuthenticatedRouteAnnotationProcessor implements RouteAnnotationProcessor {

  @Override
  public boolean isSupported(Object context, Class<?> typeClass, Method method) {
    return typeClass.isAnnotationPresent(Authenticated.class) || (method != null && method.isAnnotationPresent(Authenticated.class));
  }

  @Override
  public void process(Object context, Class<?> typeClass, Method method, RouteOptions options) {
    val typeAuthenticated = typeClass.isAnnotationPresent(Authenticated.class);
    val methodAuthenticated = method != null && method.isAnnotationPresent(Authenticated.class);
    options.auth(typeAuthenticated || methodAuthenticated);
  }

}
