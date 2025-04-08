package io.aitech.vertx.http.param.impl;

import io.aitech.vertx.http.annotation.QueryParams;
import io.aitech.vertx.http.model.ParamType;
import io.aitech.vertx.http.param.ParameterProvider;
import io.aitech.vertx.http.request.RequestReader;
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
