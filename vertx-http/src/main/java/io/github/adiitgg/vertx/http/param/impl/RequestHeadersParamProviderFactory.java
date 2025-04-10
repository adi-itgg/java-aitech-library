package io.github.adiitgg.vertx.http.param.impl;

import io.github.adiitgg.vertx.http.annotation.RequestHeaders;
import io.github.adiitgg.vertx.http.model.ParamType;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.github.adiitgg.vertx.http.request.RequestReader;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Parameter;

@RequiredArgsConstructor
public class RequestHeadersParamProviderFactory implements ParameterProvider.Factory {

  private final RequestReader requestReader;

  @Override
  public boolean isSupported(Parameter parameter) {
    return parameter.isAnnotationPresent(RequestHeaders.class);
  }

  @Override
  public ParameterProvider create(Parameter parameter) throws Throwable {
    return context -> requestReader.read(context, parameter.getType(), ParamType.HEADER);
  }

}
