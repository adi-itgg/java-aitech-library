package io.aitech.vertx.http.request.impl;

import io.aitech.vertx.http.model.ParamType;
import io.aitech.vertx.http.request.RequestReader;
import io.aitech.vertx.http.util.StringUtil;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
public class HeadersRequestReader implements RequestReader {

  private final DatabindCodec databindCodec;

  @Override
  public boolean isSupported(RoutingContext context, Type type, ParamType paramType) {
    return !context.request().headers().isEmpty() && paramType == ParamType.HEADER;
  }

  @Override
  public Object read(RoutingContext context, Type type, ParamType paramType) {
    Map<String, String> headers = new HashMap<>();
    for (Map.Entry<String, String> header : context.request().headers()) {
      headers.put(StringUtil.toCamelCase(header.getKey().toLowerCase(Locale.ROOT), '-'), header.getValue());
    }
    return databindCodec.fromValue(headers, (Class<?>) type);
  }


}
