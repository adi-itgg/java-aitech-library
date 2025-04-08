package io.aitech.vertx.http.route;

import io.vertx.core.Handler;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;


public interface MiddlewareHandler extends Handler<RoutingContext> {

    /**
     * @return null to skip and will retry the next handler
     */
    default Route onRegister(Route route) {
        return route;
    }

}
