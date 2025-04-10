package io.github.adiitgg.vertx.config.yml;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
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
    val config = """
      profile: production
      app:
        port: ${ APP_PORT | 8080 }
      yaml:
        allowDuplicateKeys: false
        wrappedToRootException: false
        maxAliasesForCollections: 64
        allowRecursiveKeys: true
        processComments: true
        enumCaseSensitive: false
        nestingDepthLimit: 128
        codePointLimit: 1048576
      """;
    assertEquals("yml", processor.name());
    processor.process(vertx, null, Buffer.buffer(config))
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
