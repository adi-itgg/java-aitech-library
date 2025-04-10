package io.github.adiitgg.vertx.http.model;

import io.github.adiitgg.vertx.http.annotation.route.Route;
import io.github.adiitgg.vertx.http.invoker.MethodInvoker;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

@Data
@Accessors(fluent = true)
public class MethodApi {

  private Route route;
  private Object context;
  private Method method;
  private RouteOptions options;
  private MethodInvoker.Factory methodInvokerFactory;

}
