package io.github.adiitgg.vertx.config.yml;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class YmlProcessorTest {

  @Test
  public void parse(Vertx vertx, VertxTestContext testContext) {
    val processor = new YmlProcessor();
    System.setProperty("profile", "development");
    System.setProperty("app.postgres", "{\"host\": \"127.0.0.1\", \"port\": 5432, \"user\": \"postgres\", \"password\": \"postgres\", \"database\": \"postgres\"}");
    val config = """
      profile: production
      app:
        port: ${ APP_PORT | 8080 }
        authentication: true
        dummy-signature: no
        keep-alive: yes
        postgres:
          host: ${ DB_HOST | 127.0.0.1 }
          port: ${ DB_PORT | 5432 }
          user: ${ DB_USER | postgres }
          password: ${ DB_PASSWORD | postgres }
          database: ${ DB_NAME | postgres }
        cors:
          allowedOrigins:
            - http://localhost:8080
          allowedHeaders:
            - X-Requested-With
            - Content-Type
          allowedMethods:
            - GET
            - POST
      """;
    val yamlConfig = JsonObject.of(
      "allowDuplicateKeys", false,
      "wrappedToRootException", false,
      "maxAliasesForCollections", 64,
      "allowRecursiveKeys", true,
      "processComments", true,
      "enumCaseSensitive", false,
      "nestingDepthLimit", 128,
      "codePointLimit", 1048576
    );
    assertEquals("yml", processor.name());
    processor.process(vertx, JsonObject.of("yaml", yamlConfig), Buffer.buffer(config))
      .onComplete(testContext.succeedingThenComplete());
  }


  @Test
  public void parseFailed(Vertx vertx, VertxTestContext testContext) {
    val processor = new YmlProcessor();
    val config = """
      profile: production
      app:
        port: ${ APP_PORT | 8080 }
      yaml
      """;
    processor.process(vertx, null, Buffer.buffer(config))
      .onComplete(testContext.failingThenComplete());
  }


}
