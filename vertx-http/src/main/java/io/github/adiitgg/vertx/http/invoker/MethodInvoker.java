package io.github.adiitgg.vertx.http.invoker;

import io.github.adiitgg.vertx.http.model.RouteOptions;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;

public interface MethodInvoker {


  Object invoke(RoutingContext context, RouteOptions options);


  interface Factory {

    boolean isSupported(Method method);


    MethodInvoker create(Object context, Method method, ParameterProvider.Factory parameterProviderFactory) throws Throwable;

  }

}
