package io.github.adiitgg.vertx.http.response;

import io.github.adiitgg.vertx.http.response.impl.DefaultResponseMapper;
import io.vertx.core.buffer.Buffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseMapperTest {

  @Test
  void map_Null() {
    ResponseMapper responseMapper = new DefaultResponseMapper();
    Buffer result = responseMapper.map(null, null);
    assertEquals("null", result.toString());
  }

  @Test
  void map_NotNull() {
    ResponseMapper responseMapper = new DefaultResponseMapper();
    Buffer result = responseMapper.map(null, new TestObject("unit-test"));
    assertEquals("{\"name\":\"unit-test\"}", result.toString());
  }

  @Getter
  @AllArgsConstructor
  public static class TestObject {

    private String name;

  }

}
