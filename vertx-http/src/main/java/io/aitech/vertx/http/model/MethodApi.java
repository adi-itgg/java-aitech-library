package io.aitech.vertx.http.model;

import io.aitech.vertx.http.annotation.route.Route;
import io.aitech.vertx.http.invoker.MethodInvoker;
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
