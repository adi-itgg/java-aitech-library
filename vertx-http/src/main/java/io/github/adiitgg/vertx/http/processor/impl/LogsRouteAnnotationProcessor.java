package io.github.adiitgg.vertx.http.processor.impl;

import io.github.adiitgg.vertx.http.annotation.route.Logs;
import io.github.adiitgg.vertx.http.model.RouteOptions;
import io.github.adiitgg.vertx.http.processor.RouteAnnotationProcessor;
import lombok.val;

import java.lang.reflect.Method;

public class LogsRouteAnnotationProcessor implements RouteAnnotationProcessor {

  @Override
  public boolean isSupported(Object context, Class<?> typeClass, Method method) {
    return typeClass.isAnnotationPresent(Logs.class) || (method != null && method.isAnnotationPresent(Logs.class));
  }

  @Override
  public void process(Object context, Class<?> typeClass, Method method, RouteOptions options) {
    val typeBlocking = typeClass.getAnnotation(Logs.class);
    val methodLogs = method == null ? null : method.getAnnotation(Logs.class);
    val disableRequestLog = typeBlocking != null && !typeBlocking.request() || methodLogs != null && !methodLogs.request();
    val disableResponseLog = typeBlocking != null && !typeBlocking.response() || methodLogs != null && !methodLogs.response();
    options.disableRequestLog(disableRequestLog);
    options.disableResponseLog(disableResponseLog);
  }

}
