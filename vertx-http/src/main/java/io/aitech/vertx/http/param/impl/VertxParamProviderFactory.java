package io.aitech.vertx.http.param.impl;

import io.aitech.vertx.http.param.ParameterProvider;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

public class VertxParamProviderFactory implements ParameterProvider.Factory {

  @Override
  public boolean isSupported(Parameter parameter) {
    return parameter.getType() == Vertx.class;
  }

  @Override
  public ParameterProvider create(Parameter parameter) throws Throwable {
    return RoutingContext::vertx;
  }

}
