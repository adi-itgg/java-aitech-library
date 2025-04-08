package io.aitech.vertx.http.response;

import io.aitech.vertx.http.model.RouteOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;

public interface ResponseWriter {

  boolean write(RoutingContext context, Buffer result);

  default boolean write(RoutingContext context, Object result, RouteOptions options) {
    throw new IllegalCallerException("Unsupported implementation");
  }

}
