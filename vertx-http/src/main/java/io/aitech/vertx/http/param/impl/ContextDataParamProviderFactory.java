package io.aitech.vertx.http.param.impl;

import io.aitech.vertx.http.annotation.ContextData;
import io.aitech.vertx.http.param.ParameterProvider;

import java.lang.reflect.Parameter;

public class ContextDataParamProviderFactory implements ParameterProvider.Factory {

  @Override
  public boolean isSupported(Parameter parameter) {
    return parameter.isAnnotationPresent(ContextData.class);
  }

  @Override
  public ParameterProvider create(Parameter parameter) throws Throwable {
    final ContextData contextData = parameter.getAnnotation(ContextData.class);
    return context -> context.get(contextData.value());
  }

}
