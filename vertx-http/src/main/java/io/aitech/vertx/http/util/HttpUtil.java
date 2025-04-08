package io.aitech.vertx.http.util;

import io.aitech.vertx.http.model.RoutingData;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class HttpUtil {

  public static boolean canEnd(RoutingContext context) {
    return !context.response().headWritten() && !context.response().ended();
  }

  public static void end(RoutingContext context, Buffer body) {
    if (!canEnd(context)) {
      return;
    }
    context.put(RoutingData.RESPONSE_BODY_BUFFER, body);
    context.response().end(body);
  }

}
