package io.github.adiitgg.vertx.http.route;

import io.github.adiitgg.vertx.http.model.MethodApi;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public interface OnRegisteringRoute {

  default Route before(Router router, String path, MethodApi methodApi) {
    return router.route();
  }

  default Route after(Route route, MethodApi methodApi) {
    return route;
  }

}
