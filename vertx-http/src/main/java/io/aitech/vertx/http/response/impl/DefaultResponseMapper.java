package io.aitech.vertx.http.response.impl;

import io.aitech.vertx.http.response.ResponseMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;

public class DefaultResponseMapper implements ResponseMapper {

  @Override
  public Buffer map(Object result) {
    return Json.encodeToBuffer(result);
  }

}
