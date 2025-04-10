package io.github.adiitgg.vertx.http.param.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.adiitgg.vertx.http.annotation.QueryParam;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Parameter;

@RequiredArgsConstructor
public class RequestQueryParamProviderFactory implements ParameterProvider.Factory {

  private final ObjectMapper objectMapper;

  @Override
  public boolean isSupported(Parameter parameter) {
    return parameter.isAnnotationPresent(QueryParam.class);
  }

  @Override
  public ParameterProvider create(Parameter parameter) throws Throwable {
    final QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
    final String key = queryParam.value().isBlank() ? parameter.getName() : queryParam.value();
    final boolean isString = parameter.getType().isAssignableFrom(String.class);

    return context -> {
      String result = context.request().getParam(key, queryParam.defaultValue());
      if (isString) {
        return result;
      }
      return objectMapper.convertValue(result, parameter.getType());
    };
  }

}
