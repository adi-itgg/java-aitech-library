package io.aitech.vertx.http.request.impl;

import io.aitech.vertx.http.model.ParamType;
import io.aitech.vertx.http.request.RequestReader;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;

@RequiredArgsConstructor
public class PathParamRequestReader implements RequestReader {

  private final DatabindCodec databindCodec;

  @Override
  public boolean isSupported(RoutingContext context, Type type, ParamType paramType) {
    return !context.pathParams().isEmpty() && paramType == ParamType.PATH;
  }

  @Override
  public Object read(RoutingContext context, Type type, ParamType paramType) {
    return databindCodec.fromValue(context.pathParams(), (Class<?>) type);
  }

}
