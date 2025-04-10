package io.github.adiitgg.vertx.http.util;

import io.github.adiitgg.vertx.http.impl.RouterBuilderImpl;
import io.github.adiitgg.vertx.http.model.Constants;
import io.vertx.core.Handler;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.RouteImpl;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@UtilityClass
public final class RouterUtil {

  @SuppressWarnings("unchecked")
  @SneakyThrows
  public static String getRouteList(List<Route> routerRoutes) {
    val sb = new StringBuilder("\r\n");

    sb.append(String.format("%-5s ", "ORDER"))
      .append(String.format("%9s ", "STATUS"))
      .append(String.format("%10s ", "METHODS"))
      .append(String.format("%-25s ", "ROUTE"))
      .append("Handlers")
      .append("\r\n");

    val stateField = RouteImpl.class.getDeclaredField("state");
    stateField.setAccessible(true);
    Method isEnabledMethod = null;
    Method contextHandlersMethod = null;
    Method orderMethod = null;

    for (Route route : routerRoutes) {
      val routeImpl = (RouteImpl) route;
      val path = routeImpl.getPath();
      val apiPath = path == null ? "" : path;

      val routeState = stateField.get(routeImpl);

      if (contextHandlersMethod == null) {
        contextHandlersMethod = routeState.getClass().getDeclaredMethod("getContextHandlers");
        contextHandlersMethod.setAccessible(true);
      }

      if (isEnabledMethod == null) {
        isEnabledMethod = routeState.getClass().getDeclaredMethod("isEnabled");
        isEnabledMethod.setAccessible(true);
      }

      if (orderMethod == null) {
        orderMethod = routeState.getClass().getDeclaredMethod("getOrder");
        orderMethod.setAccessible(true);
      }

      val order = (int) orderMethod.invoke(routeState);
      val enable = (Boolean) isEnabledMethod.invoke(routeState);
      val methods = routeImpl.methods();
      val handlers = Objects.requireNonNullElse((List<Handler<RoutingContext>>) contextHandlersMethod.invoke(routeState), Collections.emptyList())
        .stream()
        .map(h -> {
          if (h.getClass().getName().startsWith(RouterBuilderImpl.class.getName())) {
            return route.getMetadata(Constants.ROUTE_META_DATA_CLASS_METHOD_HANDLER);
          }
          return h.getClass().getName();
        }).toList();

      sb.append(String.format("%5s ", order))
        .append(String.format("%9s ", enable ? "ACTIVE" : "INACTIVE"))
        .append(String.format("%10s ", methods))
        .append(String.format("%-25s ", apiPath))
        .append(String.join(",", handlers))
        .append("\r\n");
    }
    return sb.toString();
  }

}
