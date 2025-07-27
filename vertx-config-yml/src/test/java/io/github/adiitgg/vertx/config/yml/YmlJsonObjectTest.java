package io.github.adiitgg.vertx.config.yml;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class YmlJsonObjectTest {

  @Test
  void put() {
    YmlJsonObject jsonObject = new YmlJsonObject();
    jsonObject.put("key1", "value1");
    jsonObject.put("user.name", "testing");
    // Assertions
    assertEquals("value1", jsonObject.getString("key1"));
    assertEquals("testing", jsonObject.getString("user.name"));
  }

  @Test
  void putNull() {
    YmlJsonObject jsonObject = new YmlJsonObject();
    jsonObject.putNull("key1");
    jsonObject.putNull("user.name");
    // Assertions
    assertNull(jsonObject.getString("key1"));
    assertNull(jsonObject.getString("user.name"));
  }

  @Test
  void remove() {
    YmlJsonObject jsonObject = new YmlJsonObject();
    jsonObject.put("key1", "value1");
    jsonObject.put("user.name", "testing");
    // Assertions
    assertEquals("value1", jsonObject.remove("key1"));
    assertNull(jsonObject.getValue("key1"));
    assertEquals("testing", jsonObject.remove("user.name"));
    assertNull(jsonObject.getString("user.name"));
  }

  @Test
  void jsonObjectToYmlJsonObject() {
    JsonObject jsonObject = JsonObject.of("key1", "value1", "user.name", "testing");
    JsonObject nestedJsonObject = JsonObject.of("key2", "value2");
    jsonObject.put("nested", nestedJsonObject);
    YmlJsonObject ymlJsonObject = YmlJsonObject.of(jsonObject);
    // Assertions
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("testing", ymlJsonObject.getString("user.name"));
  }

  @Test
  void jsonFromStringToYmlJsonObject() {
    String json = "{\"key1\": \"value1\", \"user.name\": \"testing\"}";
    YmlJsonObject ymlJsonObject = new YmlJsonObject(json);
    // Assertions
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("testing", ymlJsonObject.getString("user.name"));
  }

  @Test
  void jsonFromBufferToYmlJsonObject() {
    Buffer json = Buffer.buffer("{\"key1\": \"value1\", \"user.name\": \"testing\"}");
    YmlJsonObject ymlJsonObject = new YmlJsonObject(json);
    // Assertions
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("testing", ymlJsonObject.getString("user.name"));
  }

  @Test
  void ymlJsonObjectOfArgs() {
    YmlJsonObject ymlJsonObject = YmlJsonObject.of("key1", "value1");
    assertEquals("value1", ymlJsonObject.getString("key1"));

    ymlJsonObject = YmlJsonObject.of("key1", "value1", "user.name", "testing");
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("testing", ymlJsonObject.getString("user.name"));

    ymlJsonObject = YmlJsonObject.of("key1", "value1", "user.name", "testing", "key2", "value2");
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("testing", ymlJsonObject.getString("user.name"));
    assertEquals("value2", ymlJsonObject.getString("key2"));

    ymlJsonObject = YmlJsonObject.of("key1", "value1", "user.name", "testing", "key2", "value2", "key3", "value3");
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("testing", ymlJsonObject.getString("user.name"));
    assertEquals("value2", ymlJsonObject.getString("key2"));
    assertEquals("value3", ymlJsonObject.getString("key3"));

    ymlJsonObject = YmlJsonObject.of("key1", "value1", "user.name", "testing", "key2", "value2", "key3", "value3", "key4", "value4");
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("testing", ymlJsonObject.getString("user.name"));
    assertEquals("value2", ymlJsonObject.getString("key2"));
    assertEquals("value3", ymlJsonObject.getString("key3"));
    assertEquals("value4", ymlJsonObject.getString("key4"));


    ymlJsonObject = YmlJsonObject.of("key1", "value1", "user.name", "testing", "key2", "value2", "key3", "value3", "key4", "value4", "key5", "value5");
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("testing", ymlJsonObject.getString("user.name"));
    assertEquals("value2", ymlJsonObject.getString("key2"));
    assertEquals("value3", ymlJsonObject.getString("key3"));
    assertEquals("value4", ymlJsonObject.getString("key4"));
    assertEquals("value5", ymlJsonObject.getString("key5"));

    ymlJsonObject = YmlJsonObject.of("key1", "value1", "user.name", "testing", "key2", "value2", "key3", "value3", "key4", "value4", "key5", "value5", "key6", "value6");
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("testing", ymlJsonObject.getString("user.name"));
    assertEquals("value2", ymlJsonObject.getString("key2"));
    assertEquals("value3", ymlJsonObject.getString("key3"));
    assertEquals("value4", ymlJsonObject.getString("key4"));
    assertEquals("value5", ymlJsonObject.getString("key5"));
    assertEquals("value6", ymlJsonObject.getString("key6"));

    ymlJsonObject = YmlJsonObject.of("key1", "value1", "user.name", "testing", "key2", "value2", "key3", "value3", "key4", "value4", "key5", "value5", "key6", "value6", "key7", "value7");
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("testing", ymlJsonObject.getString("user.name"));
    assertEquals("value2", ymlJsonObject.getString("key2"));
    assertEquals("value3", ymlJsonObject.getString("key3"));
    assertEquals("value4", ymlJsonObject.getString("key4"));
    assertEquals("value5", ymlJsonObject.getString("key5"));
    assertEquals("value6", ymlJsonObject.getString("key6"));
    assertEquals("value7", ymlJsonObject.getString("key7"));

    ymlJsonObject = YmlJsonObject.of("key1", "value1", "user.name", "testing", "key2", "value2", "key3", "value3", "key4", "value4", "key5", "value5", "key6", "value6", "key7", "value7", "key8", "value8");
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("testing", ymlJsonObject.getString("user.name"));
    assertEquals("value2", ymlJsonObject.getString("key2"));
    assertEquals("value3", ymlJsonObject.getString("key3"));
    assertEquals("value4", ymlJsonObject.getString("key4"));
    assertEquals("value5", ymlJsonObject.getString("key5"));
    assertEquals("value6", ymlJsonObject.getString("key6"));
    assertEquals("value7", ymlJsonObject.getString("key7"));
    assertEquals("value8", ymlJsonObject.getString("key8"));

    ymlJsonObject = YmlJsonObject.of("key1", "value1", "user.name", "testing", "key2", "value2", "key3", "value3", "key4", "value4", "key5", "value5", "key6", "value6", "key7", "value7", "key8", "value8", "key9", "value9");
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("testing", ymlJsonObject.getString("user.name"));
    assertEquals("value2", ymlJsonObject.getString("key2"));
    assertEquals("value3", ymlJsonObject.getString("key3"));
    assertEquals("value4", ymlJsonObject.getString("key4"));
    assertEquals("value5", ymlJsonObject.getString("key5"));
    assertEquals("value6", ymlJsonObject.getString("key6"));
    assertEquals("value7", ymlJsonObject.getString("key7"));
    assertEquals("value8", ymlJsonObject.getString("key8"));
    assertEquals("value9", ymlJsonObject.getString("key9"));

    ymlJsonObject = YmlJsonObject.of("key1", "value1", "user.name", "testing", "key2", "value2", "key3", "value3", "key4", "value4", "key5", "value5", "key6", "value6", "key7", "value7", "key8", "value8", "key9", "value9", "key10", "value10");
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("testing", ymlJsonObject.getString("user.name"));
    assertEquals("value2", ymlJsonObject.getString("key2"));
    assertEquals("value3", ymlJsonObject.getString("key3"));
    assertEquals("value4", ymlJsonObject.getString("key4"));
    assertEquals("value5", ymlJsonObject.getString("key5"));
    assertEquals("value6", ymlJsonObject.getString("key6"));
    assertEquals("value7", ymlJsonObject.getString("key7"));
    assertEquals("value8", ymlJsonObject.getString("key8"));
    assertEquals("value9", ymlJsonObject.getString("key9"));
    assertEquals("value10", ymlJsonObject.getString("key10"));


    ymlJsonObject = YmlJsonObject.of("key1", "value1", "key2", "value2", "key3", "value3", "key4", "value4", "key5", "value5", "key6", "value6", "key7", "value7", "key8", "value8", "key9", "value9", "key10", "value10", "key11", "value11");
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("value2", ymlJsonObject.getString("key2"));
    assertEquals("value3", ymlJsonObject.getString("key3"));
    assertEquals("value4", ymlJsonObject.getString("key4"));
    assertEquals("value5", ymlJsonObject.getString("key5"));
    assertEquals("value6", ymlJsonObject.getString("key6"));
    assertEquals("value7", ymlJsonObject.getString("key7"));
    assertEquals("value8", ymlJsonObject.getString("key8"));
    assertEquals("value9", ymlJsonObject.getString("key9"));
    assertEquals("value10", ymlJsonObject.getString("key10"));
    assertEquals("value11", ymlJsonObject.getString("key11"));
  }

  @Test
  void ymlJsonObjectInvalidOfArgs() {
    try {
      YmlJsonObject.of("key1", "value1", "key2", "value2", "key3", "value3", "key4", "value4", "key5", "value5", "key6", "value6", "key7", "value7", "key8", "value8", "key9", "value9", "key10", "value10", "key11");
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid number of arguments", e.getMessage());
    }
  }

  @Test
  void mapFrom_null() {
    assertNull(YmlJsonObject.mapFrom(null));
  }

  @Test
  void mapFrom_success() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("key1", "value1");
    YmlJsonObject ymlJsonObject = YmlJsonObject.mapFrom(map);
    assertEquals("value1", ymlJsonObject.getString("key1"));
  }

  @Test
  void getJsonObjectDefault() {
    YmlJsonObject ymlJsonObject = new YmlJsonObject();
    YmlJsonObject jsonObject = ymlJsonObject.getJsonObject("key1", YmlJsonObject.of());
    assertNotNull(jsonObject);
  }

  @Test
  void mergeIn_success() {
    YmlJsonObject ymlJsonObject = new YmlJsonObject();
    ymlJsonObject.mergeIn(YmlJsonObject.of("key1", "value1"));
    ymlJsonObject.mergeIn(YmlJsonObject.of("key2", "value2"));
    assertEquals("value1", ymlJsonObject.getString("key1"));
    assertEquals("value2", ymlJsonObject.getString("key2"));
  }

  @Test
  void mergeIn_deep_success() {
    YmlJsonObject ymlJsonObject = new YmlJsonObject();
    ymlJsonObject.mergeIn(YmlJsonObject.of("key1", "value1"), true);
    assertEquals("value1", ymlJsonObject.getString("key1"));
  }

  @Test
  void mergeIn_depth1_success() {
    YmlJsonObject ymlJsonObject = new YmlJsonObject();
    ymlJsonObject.mergeIn(YmlJsonObject.of("key1", "value1"), 1);
    assertEquals("value1", ymlJsonObject.getString("key1"));
  }

  @Test
  void copy_success() {
    YmlJsonObject ymlJsonObject = new YmlJsonObject();
    ymlJsonObject.mergeIn(YmlJsonObject.of("key1", "value1"), 1);
    YmlJsonObject ymlJsonObjectCopy = ymlJsonObject.copy();
    assertEquals("value1", ymlJsonObjectCopy.getString("key1"));
  }

  @Test
  void getYmlJsonObject() {
    YmlJsonObject ymlJsonObject = new YmlJsonObject();
    ymlJsonObject.put("key1", YmlJsonObject.of("key1", "value1"));
    YmlJsonObject ymlJsonObjectCopy = ymlJsonObject.getYmlJsonObject("key1");
    assertEquals("value1", ymlJsonObjectCopy.getString("key1"));
  }

  @Test
  void getYmlJsonObjectDefault() {
    YmlJsonObject ymlJsonObject = new YmlJsonObject();
    assertNotNull(ymlJsonObject.getYmlJsonObject("key1", YmlJsonObject.of()));
  }

}
