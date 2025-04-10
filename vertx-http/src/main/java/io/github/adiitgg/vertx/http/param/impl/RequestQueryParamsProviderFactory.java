package io.github.adiitgg.vertx.http.param.impl;

import io.github.adiitgg.vertx.http.annotation.QueryParams;
import io.github.adiitgg.vertx.http.model.ParamType;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.github.adiitgg.vertx.http.request.RequestReader;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Parameter;

@RequiredArgsConstructor
public class RequestQueryParamsProviderFactory implements ParameterProvider.Factory {

  private final RequestReader requestReader;

  @Override
  public boolean isSupported(Parameter parameter) {
    return parameter.isAnnotationPresent(QueryParams.class);
  }

  @Override
  public ParameterProvider create(Parameter parameter) throws Throwable {
    return context -> requestReader.read(context, parameter.getType(), ParamType.QUERY);
  }

}
