package io.github.adiitgg.vertx.http.response;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;

public interface ResponseMapper {

  Buffer map(RoutingContext context, Object result);

}
