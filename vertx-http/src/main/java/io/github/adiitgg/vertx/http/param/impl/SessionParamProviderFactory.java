package io.github.adiitgg.vertx.http.param.impl;

import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.lang.reflect.Parameter;

public class SessionParamProviderFactory implements ParameterProvider.Factory {

  @Override
  public boolean isSupported(Parameter parameter) {
    return parameter.getType() == Session.class;
  }

  @Override
  public ParameterProvider create(Parameter parameter) throws Throwable {
    return RoutingContext::session;
  }

}
