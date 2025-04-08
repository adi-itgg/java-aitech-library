package io.aitech.vertx.http.response;

import io.vertx.core.buffer.Buffer;

public interface ResponseMapper {

  Buffer map(Object result);

}
