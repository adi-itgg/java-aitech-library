package io.github.adiitgg.vertx.config.impl;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class ConfigRetrieverExtendedImplTest {

  private ConfigRetrieverExtendedImpl retriever;

  @BeforeEach
  public void setup(Vertx vertx) {
    ConfigRetrieverOptions options = new ConfigRetrieverOptions();
    retriever = new ConfigRetrieverExtendedImpl(vertx, options);
  }

  @Test
  public void testConstructorWithOnlyVertx(Vertx vertx) {
    ConfigRetrieverExtendedImpl r = new ConfigRetrieverExtendedImpl(vertx);
    assertNotNull(r);
  }

  @Test
  public void testClosePromise() {
    Promise<Void> promise = Promise.promise();
    retriever.close(promise);
    assertTrue(promise.future().succeeded());
  }

  @Test
  public void testCloseOnceOnly() {
    retriever.close(); // first time
    retriever.close(); // should not throw
  }

  @Test
  public void testClassLoaderSetter() {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    ConfigRetrieverExtendedImpl result = retriever.classLoader(cl);
    assertEquals(retriever, result);
  }

  @Test
  public void testArgsSetter() {
    String[] args = {"--profiles=dev,test"};
    ConfigRetrieverExtendedImpl result = retriever.args(args);
    assertEquals(retriever, result);
  }

  @Test
  public void testFormatSetter() {
    ConfigRetrieverExtendedImpl result = retriever.format("json");
    assertEquals(retriever, result);
  }

  @Test
  public void testFormatSetterWithNullThrows() {
    assertThrows(IllegalArgumentException.class, () -> retriever.format(null));
  }

  @Test
  public void testConfigSetter() {
    JsonObject config = new JsonObject().put("foo", "bar");
    ConfigRetrieverExtendedImpl result = retriever.config(config);
    assertEquals(retriever, result);
  }

  @Test
  public void testConfigSetterWithNullThrows() {
    assertThrows(IllegalArgumentException.class, () -> retriever.config(null));
  }

  @Test
  public void testGetDefaultConfigPath_EnvEmpty() {
    System.clearProperty("vertx-config-path");
    String result = retriever.format("yml").getDefaultConfigPath("yml");
    assertNotNull(result);
  }

  @Test
  public void testGetDefaultDirPath_EnvEmpty() {
    System.clearProperty("vertx-config-dir-path");
    String result = retriever.getDefaultDirPath();
    assertNotNull(result);
  }

  @Test
  public void testGetDefaultProfile_Empty() {
    System.clearProperty("vertx-profiles");
    String result = retriever.getDefaultProfile();
    assertEquals("", result);
  }
}
