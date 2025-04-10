package io.github.adiitgg.vertx.http.param.impl;

import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

public class HttpServerResponseParamProviderFactory implements ParameterProvider.Factory {

  @Override
  public boolean isSupported(Parameter parameter) {
    return parameter.getType() == HttpServerResponse.class;
  }

  @Override
  public ParameterProvider create(Parameter parameter) throws Throwable {
    return RoutingContext::response;
  }

}
