package io.github.adiitgg.vertx.http.response.impl;

import io.github.adiitgg.vertx.http.model.RouteOptions;
import io.github.adiitgg.vertx.http.model.RoutingData;
import io.github.adiitgg.vertx.http.response.ResponseMapper;
import io.github.adiitgg.vertx.http.response.ResponseWriter;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeResponseWriter implements ResponseWriter {

  private final ResponseWriter[] responseWriters;
  private final ResponseMapper responseMapper;

  @Override
  public boolean write(RoutingContext context, Buffer result) {
    for (ResponseWriter writer : responseWriters) {
      if (writer.write(context, result)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean write(RoutingContext context, Object result, RouteOptions options) {
    if (result instanceof Buffer buffer) {
      context.put(RoutingData.RESPONSE_BODY_BUFFER, buffer);
      return write(context, buffer);
    }
    Buffer buffer = responseMapper.map(context, result);
    context.put(RoutingData.RESPONSE_BODY_BUFFER, buffer);
    return write(context, buffer);
  }

}
