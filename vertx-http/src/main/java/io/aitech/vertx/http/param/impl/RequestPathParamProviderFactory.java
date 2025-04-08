package io.aitech.vertx.http.param.impl;

import io.aitech.vertx.http.annotation.PathParam;
import io.aitech.vertx.http.model.ParamType;
import io.aitech.vertx.http.param.ParameterProvider;
import io.aitech.vertx.http.request.RequestReader;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Parameter;

@RequiredArgsConstructor
public class RequestPathParamProviderFactory implements ParameterProvider.Factory {

  private final RequestReader requestReader;

  @Override
  public boolean isSupported(Parameter parameter) {
    return parameter.isAnnotationPresent(PathParam.class);
  }

  @Override
  public ParameterProvider create(Parameter parameter) throws Throwable {
    return context -> requestReader.read(context, parameter.getType(), ParamType.PATH);
  }

}
