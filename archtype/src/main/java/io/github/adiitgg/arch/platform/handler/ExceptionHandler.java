package io.github.adiitgg.arch.platform.handler;

import io.github.adiitgg.arch.platform.exception.InfoLevelException;
import io.github.adiitgg.arch.platform.model.BaseResponse;
import io.github.adiitgg.vertx.http.exception.ViolationException;
import io.avaje.inject.Component;
import io.avaje.inject.aop.InvocationException;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Function;

@Slf4j
@Component
public class ExceptionHandler implements Function<RoutingContext, Buffer> {

  @Override
  public Buffer apply(RoutingContext context) {
    var exception = context.failure();
    if (exception instanceof InvocationException && exception.getCause() != null) {
      exception = exception.getCause();
    }

    if (exception instanceof ViolationException e) {
      exception = new InfoLevelException(e.getMessage());
    }

    if (exception instanceof DecodeException e) {
      exception = new InfoLevelException("Bad request! - " + e.getCause().getMessage().split(":")[0]);
    }

    if (exception instanceof InfoLevelException i) {
      context.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
      return new BaseResponse()
        .code(HttpResponseStatus.BAD_REQUEST.codeAsText().toString())
        .message(i.getMessage())
        .toBuffer();
    }

    if (exception instanceof InterruptedException) {
      return new BaseResponse()
        .code(HttpResponseStatus.UNPROCESSABLE_ENTITY.codeAsText().toString())
        .message("Server might have been shut down!")
        .toBuffer();
    }


    if (exception instanceof IOException) {
      context.response().reset(); // reset because has IO Error!
      return Buffer.buffer("I/O Error");
    }

    log.error("Internal Server Errror! - {}", exception.getMessage(), exception);
    return new BaseResponse()
      .code(HttpResponseStatus.INTERNAL_SERVER_ERROR.codeAsText().toString())
      .message("A system error has occurred, preventing the completion of your request. Please wait a few moments and try again. If the issue persists, contact our support team for assistance.")
      .toBuffer();
  }

}
