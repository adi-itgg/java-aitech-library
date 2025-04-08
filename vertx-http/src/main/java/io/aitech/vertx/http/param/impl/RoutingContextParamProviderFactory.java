package io.aitech.vertx.http.param.impl;

import io.aitech.vertx.http.param.ParameterProvider;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

public class RoutingContextParamProviderFactory implements ParameterProvider.Factory {

  @Override
  public boolean isSupported(Parameter parameter) {
    return parameter.getType() == RoutingContext.class;
  }

  @Override
  public ParameterProvider create(Parameter parameter) throws Throwable {
    return context -> context;
  }

}
