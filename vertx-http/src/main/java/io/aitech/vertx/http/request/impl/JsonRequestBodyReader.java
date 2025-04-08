package io.aitech.vertx.http.request.impl;

import io.aitech.vertx.http.model.ParamType;
import io.aitech.vertx.http.request.RequestReader;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;

@RequiredArgsConstructor
public class JsonRequestBodyReader implements RequestReader {

  private final DatabindCodec databindCodec;

  @Override
  public boolean isSupported(RoutingContext context, Type type, ParamType paramType) {
    String contentType = context.request().getHeader(HttpHeaders.CONTENT_TYPE);
    return contentType != null && contentType.startsWith(HttpHeaderValues.APPLICATION_JSON.toString()) && paramType == ParamType.BODY;
  }

  @Override
  public Object read(RoutingContext context, Type type, ParamType paramType) {
    Buffer body = context.body().buffer();
    if (body == null) {
      return null;
    }
    return databindCodec.fromBuffer(body, (Class<?>) type);
  }

}
