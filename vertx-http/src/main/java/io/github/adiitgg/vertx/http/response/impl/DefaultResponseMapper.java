package io.github.adiitgg.vertx.http.response.impl;

import io.github.adiitgg.vertx.http.response.ResponseMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;

public class DefaultResponseMapper implements ResponseMapper {

  @Override
  public Buffer map(Object result) {
    return Json.encodeToBuffer(result);
  }

}
