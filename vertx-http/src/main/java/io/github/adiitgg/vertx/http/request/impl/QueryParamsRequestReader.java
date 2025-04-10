package io.github.adiitgg.vertx.http.request.impl;

import io.github.adiitgg.vertx.http.model.ParamType;
import io.github.adiitgg.vertx.http.request.RequestReader;
import io.github.adiitgg.vertx.http.util.StringUtil;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
public class QueryParamsRequestReader implements RequestReader {

  private final DatabindCodec databindCodec;

  @Override
  public boolean isSupported(RoutingContext context, Type type, ParamType paramType) {
    return !context.request().headers().isEmpty() && paramType == ParamType.QUERY;
  }

  @Override
  public Object read(RoutingContext context, Type type, ParamType paramType) {
    Map<String, String> params = new HashMap<>();
    for (Map.Entry<String, String> header : context.request().params()) {
      params.put(StringUtil.toCamelCase(header.getKey().toLowerCase(Locale.ROOT), '-'), header.getValue());
    }
    return databindCodec.fromValue(params, (Class<?>) type);
  }

}
