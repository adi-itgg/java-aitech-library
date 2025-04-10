package io.github.adiitgg.vertx.http.impl;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.DecodeException;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.util.function.Function;

public class DefaultExceptionHandler implements Function<RoutingContext, Buffer> {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public Buffer apply(RoutingContext context) {
    var exception = context.failure();

    if (exception instanceof InterruptedException) {
      return Buffer.buffer("SERVER CLOSED!");
    }

    if (exception instanceof DecodeException) {
      log.info("error parsing data: " + exception.getMessage());
      return Buffer.buffer("Invalid Request!");
    }

    if (exception instanceof IOException) {
      context.response().reset();
      return null;
    }

    return null;
  }

}
