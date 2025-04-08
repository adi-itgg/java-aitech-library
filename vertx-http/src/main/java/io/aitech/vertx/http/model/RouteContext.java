package io.aitech.vertx.http.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class RouteContext {

  private Object context;
  private RouteOptions options;

}
