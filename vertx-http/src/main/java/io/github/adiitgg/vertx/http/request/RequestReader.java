package io.github.adiitgg.vertx.http.request;

import io.github.adiitgg.vertx.http.model.ParamType;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Type;

public interface RequestReader {

  boolean isSupported(RoutingContext context, Type type, ParamType paramType);

  Object read(RoutingContext context, Type type, ParamType paramType);

}
