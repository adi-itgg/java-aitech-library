package io.github.adiitgg.vertx.http.response.impl;

import io.github.adiitgg.vertx.http.response.ResponseMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class DefaultResponseMapper implements ResponseMapper {

  @Override
  public Buffer map(RoutingContext context, Object result) {
    return Json.encodeToBuffer(result);
  }

}
