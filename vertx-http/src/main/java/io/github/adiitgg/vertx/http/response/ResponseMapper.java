package io.github.adiitgg.vertx.http.response;

import io.vertx.core.buffer.Buffer;

public interface ResponseMapper {

  Buffer map(Object result);

}
