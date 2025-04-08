package io.aitech.vertx.http.param.impl;

import io.aitech.vertx.http.param.ParameterProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.UserContext;

import java.lang.reflect.Parameter;

public class UserContextParamProviderFactory implements ParameterProvider.Factory {

  @Override
  public boolean isSupported(Parameter parameter) {
    return parameter.getType() == UserContext.class;
  }

  @Override
  public ParameterProvider create(Parameter parameter) throws Throwable {
    return RoutingContext::userContext;
  }

}
