package io.aitech.vertx.http.request.impl;

import io.aitech.vertx.http.model.ParamType;
import io.aitech.vertx.http.request.RequestReader;
import io.aitech.vertx.http.request.RequestValidation;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.List;

@RequiredArgsConstructor
public class CompositeRequestReader implements RequestReader {

  private final List<RequestReader> requestReaders;
  private final List<RequestValidation> requestValidations;

  @Override
  public boolean isSupported(RoutingContext context, Type type, ParamType paramType) {
    for (RequestReader reader : requestReaders) {
      if (reader.isSupported(context, type, paramType)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Object read(RoutingContext context, Type type, ParamType paramType) {
    for (RequestReader reader : requestReaders) {
      if (reader.isSupported(context, type, paramType)) {
        Object result = reader.read(context, type, paramType);
        if (result != null) {
          for (RequestValidation requestValidation : requestValidations) {
            requestValidation.validate(result);
          }
        }
        return result;
      }
    }
    return null;
  }

}
