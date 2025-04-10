package io.github.adiitgg.vertx.config;

import io.github.adiitgg.vertx.config.impl.ConfigRetrieverExtendedImpl;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public interface ConfigRetrieverExtended extends ConfigRetriever {

  static ConfigRetrieverExtended create(Vertx vertx, ConfigRetrieverOptions options) {
    return new ConfigRetrieverExtendedImpl(vertx, options);
  }

  static ConfigRetrieverExtended create(Vertx vertx) {
    return create(vertx, (new ConfigRetrieverOptions()).setIncludeDefaultStores(false).setScanPeriod(-1L));
  }

  ConfigRetrieverExtended classLoader(ClassLoader classLoader);

  ConfigRetrieverExtended args(String[] args);

  ConfigRetrieverExtended format(String format);

  ConfigRetrieverExtended config(JsonObject config);

  ConfigRetrieverExtended build();

}
