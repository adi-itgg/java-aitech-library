package io.github.adiitgg.vertx.http.param.impl;

import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

public class HttpServerRequestParamProviderFactory implements ParameterProvider.Factory {

  @Override
  public boolean isSupported(Parameter parameter) {
    return parameter.getType() == HttpServerRequest.class;
  }

  @Override
  public ParameterProvider create(Parameter parameter) throws Throwable {
    return RoutingContext::request;
  }

}
