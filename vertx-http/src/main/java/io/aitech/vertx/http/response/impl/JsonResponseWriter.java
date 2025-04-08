package io.aitech.vertx.http.response.impl;

import io.aitech.vertx.http.response.ResponseWriter;
import io.aitech.vertx.http.util.HttpUtil;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

public class JsonResponseWriter implements ResponseWriter {

  @Override
  public boolean write(RoutingContext context, Buffer result) {
    if (result == null) {
      return false;
    }

    final String contentType = context.getAcceptableContentType();
    if (!HttpHeaderValues.APPLICATION_JSON.toString().equals(contentType)) {
      return false;
    }

    context.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
    HttpUtil.end(context, result);
    return true;
  }


}
