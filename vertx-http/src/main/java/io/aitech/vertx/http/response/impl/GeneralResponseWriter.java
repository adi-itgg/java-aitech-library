package io.aitech.vertx.http.response.impl;

import io.aitech.vertx.http.response.ResponseWriter;
import io.aitech.vertx.http.util.HttpUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;

public class GeneralResponseWriter implements ResponseWriter {

  @Override
  public boolean write(RoutingContext context, Buffer result) {
    if (result == null) {
      HttpUtil.end(context, Buffer.buffer());
      return true;
    }

    HttpUtil.end(context, result);
    return true;
  }


}
