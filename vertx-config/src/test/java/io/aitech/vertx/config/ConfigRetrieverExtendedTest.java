package io.aitech.vertx.config;

import io.aitech.vertx.config.yml.YmlJsonObject;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class ConfigRetrieverExtendedTest {

  @Test
  void overrideConfiguration(Vertx vertx) {
    val configurationRetrieverExtended = ConfigRetrieverExtended.create(vertx)
      .args(new String[]{"--profiles=development"})
      .build();
    val config = YmlJsonObject.of(configurationRetrieverExtended.getConfig().toCompletionStage().toCompletableFuture().join());
    assert config.getString("profile").equals("development");
    assert config.getInteger("app.port").equals(8080);
  }


  @Test
  void profileNotExists(Vertx vertx) {
    val configurationRetrieverExtended = ConfigRetrieverExtended.create(vertx)
      .args(new String[]{"--profiles=production"})
      .build();
    val config = YmlJsonObject.of(configurationRetrieverExtended.getConfig().toCompletionStage().toCompletableFuture().join());
    assert config.getInteger("app.port").equals(8080);
  }

}
