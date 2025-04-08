package io.aitech.vertx.config.yml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YmlJsonObjectTest {

  @Test
  void put() {
    // Test the put method with different types of values
    YmlJsonObject jsonObject = new YmlJsonObject();
    jsonObject.put("key1", "value1");
    jsonObject.put("user.name", "testing");
    // Assertions
    assertEquals("value1", jsonObject.getString("key1"));
    assertEquals("testing", jsonObject.getString("user.name"));
  }

  @Test
  void remove() {
      // Test the remove method with different types of values
      YmlJsonObject jsonObject = new YmlJsonObject();
      jsonObject.put("key1", "value1");
      jsonObject.put("user.name", "testing");
      // Assertions
      assertEquals("value1", jsonObject.remove("key1"));
      assertEquals(null, jsonObject.getValue("key1"));
      assertEquals("testing", jsonObject.remove("user.name"));
      assertEquals(null, jsonObject.getString("user.name"));
  }

}
