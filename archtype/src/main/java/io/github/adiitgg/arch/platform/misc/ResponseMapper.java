package io.github.adiitgg.arch.platform.misc;

import io.github.adiitgg.arch.platform.model.BaseResponse;
import io.avaje.inject.Component;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;

@Component
public class ResponseMapper implements io.github.adiitgg.vertx.http.response.ResponseMapper {

  @Override
  public Buffer map(Object result) {
    if (result instanceof Buffer buffer) {
      return buffer;
    }
    return new BaseResponse()
      .code(HttpResponseStatus.OK.codeAsText().toString())
      .message(HttpResponseStatus.OK.reasonPhrase())
      .data(result)
      .toBuffer();
  }

}
