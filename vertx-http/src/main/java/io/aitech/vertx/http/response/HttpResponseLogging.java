package io.aitech.vertx.http.response;

import io.vertx.ext.web.RoutingContext;

public interface HttpResponseLogging {

  void log(RoutingContext context, Object data);

}
