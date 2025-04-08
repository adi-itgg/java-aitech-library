package io.aitech.vertx.http.param;

import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface ParameterProvider {

  Object provide(RoutingContext context);


  interface Factory {

    boolean isSupported(Parameter parameter);

    ParameterProvider create(Parameter parameter) throws Throwable;

    default ParameterProvider[] provideParameters(Method method) {
      throw new IllegalCallerException("Unsupported implementation");
    }

  }

}
