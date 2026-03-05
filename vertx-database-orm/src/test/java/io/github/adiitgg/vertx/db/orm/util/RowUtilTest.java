package io.github.adiitgg.vertx.db.orm.util;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RowUtilTest {

  @Mock
  private Row row;

  @Test
  void testToJsonFirstOrNull_EmptyRows() {
    Iterable<Row> rows = Collections.emptyList();
    assertNull(RowUtil.toJsonFirstOrNull(rows));
    assertNull(RowUtil.toJsonFirstOrNull(rows, true));
  }

  @Test
  void testToJsonFirstOrNull_NullRows() {
    assertNull(RowUtil.toJsonFirstOrNull(null));
    assertNull(RowUtil.toJsonFirstOrNull(null, true));
  }

  @Test
  void testToJsonFirstOrNull_HasRow() {
    when(row.size()).thenReturn(2);
    when(row.getColumnName(0)).thenReturn("first_name");
    when(row.getColumnName(1)).thenReturn("buffer_data");
    when(row.getValue(0)).thenReturn("John");
    when(row.getValue(1)).thenReturn(Buffer.buffer("test"));
    List<Row> rows = List.of(row);

    JsonObject result = RowUtil.toJsonFirstOrNull(rows);
    assertNotNull(result);
    assertEquals("John", result.getString("firstName"));
    assertNotNull(result.getBuffer("bufferData"));
  }

  @Test
  void testToJsonFirstOrNull_NoCamelCase() {
    when(row.size()).thenReturn(1);
    when(row.getColumnName(0)).thenReturn("first_name");
    when(row.getValue(0)).thenReturn("John");
    List<Row> rows = List.of(row);

    JsonObject result = RowUtil.toJsonFirstOrNull(rows, false);
    assertNotNull(result);
    assertEquals("John", result.getString("first_name"));
  }

  @Test
  void testToJsonFirst_EmptyRows() {
    Iterable<Row> rows = Collections.emptyList();
    assertThrows(NoSuchElementException.class, () -> RowUtil.toJsonFirst(rows));
    assertThrows(NoSuchElementException.class, () -> RowUtil.toJsonFirst(rows, true));
    assertThrows(NoSuchElementException.class, () -> RowUtil.toJsonFirst(null));
  }

  @Test
  void testToJsonFirst_HasRow() {
    when(row.size()).thenReturn(1);
    when(row.getColumnName(0)).thenReturn("first_name");
    when(row.getValue(0)).thenReturn("John");
    List<Row> rows = List.of(row);

    JsonObject result = RowUtil.toJsonFirst(rows);
    assertNotNull(result);
    assertEquals("John", result.getString("firstName"));

    JsonObject resultFalse = RowUtil.toJsonFirst(rows, false);
    assertNotNull(resultFalse);
    assertEquals("John", resultFalse.getString("first_name"));
  }

  @Test
  void testToJson_Iterable() {
    when(row.size()).thenReturn(1);
    when(row.getColumnName(0)).thenReturn("first_name");
    when(row.getValue(0)).thenReturn("John");
    List<Row> rows = List.of(row, row);

    JsonArray result = RowUtil.toJson(rows);
    assertEquals(2, result.size());
    assertEquals("John", result.getJsonObject(0).getString("firstName"));

    JsonArray resultFalse = RowUtil.toJson(rows, false);
    assertEquals(2, resultFalse.size());
    assertEquals("John", resultFalse.getJsonObject(0).getString("first_name"));
  }

  @Test
  void testToJson_RowAllValueTypes() {
    when(row.size()).thenReturn(11);
    when(row.getColumnName(0)).thenReturn("str");
    when(row.getValue(0)).thenReturn("text");
    when(row.getColumnName(1)).thenReturn("bool");
    when(row.getValue(1)).thenReturn(true);
    when(row.getColumnName(2)).thenReturn("num");
    when(row.getValue(2)).thenReturn(123);
    when(row.getColumnName(3)).thenReturn("buf");
    when(row.getValue(3)).thenReturn(Buffer.buffer("buf"));
    when(row.getColumnName(4)).thenReturn("obj");
    when(row.getValue(4)).thenReturn(new JsonObject().put("k", "v"));
    when(row.getColumnName(5)).thenReturn("arr");
    when(row.getValue(5)).thenReturn(new JsonArray().add("v"));
    when(row.getColumnName(6)).thenReturn("null_val");
    when(row.getValue(6)).thenReturn(null);
    when(row.getColumnName(7)).thenReturn("json_null");
    when(row.getValue(7)).thenReturn(Tuple.JSON_NULL);
    when(row.getColumnName(8)).thenReturn("primitive_array");
    when(row.getValue(8)).thenReturn(new int[] { 1, 2 });
    when(row.getColumnName(9)).thenReturn("object_array");
    when(row.getValue(9)).thenReturn(new String[] { "a", "b" });
    when(row.getColumnName(10)).thenReturn("instant");
    when(row.getValue(10)).thenReturn(Instant.ofEpochSecond(1600000000));

    JsonObject json = RowUtil.toJson(row);
    assertEquals("text", json.getString("str"));
    assertEquals(true, json.getBoolean("bool"));
    assertEquals(123, json.getInteger("num"));
    assertNotNull(json.getBuffer("buf"));
    assertEquals("v", json.getJsonObject("obj").getString("k"));
    assertEquals("v", json.getJsonArray("arr").getString(0));
    assertNull(json.getString("nullVal"));
    assertNull(json.getString("jsonNull"));

    JsonArray primitiveArray = json.getJsonArray("primitiveArray");
    assertEquals(1, primitiveArray.getInteger(0));
    assertEquals(2, primitiveArray.getInteger(1));

    JsonArray objectArray = json.getJsonArray("objectArray");
    assertEquals("a", objectArray.getString(0));
    assertEquals("b", objectArray.getString(1));

    assertEquals("2020-09-13T12:26:40Z", json.getString("instant"));
  }

  @Test
  void testToJson_TemporalNotInstant() {
    when(row.size()).thenReturn(1);
    when(row.getColumnName(0)).thenReturn("local_date");
    when(row.getValue(0)).thenReturn(LocalDate.of(2020, 1, 1));

    // LocalDate isn't supported by Jackson without JSR310 module, it throws an
    // exception during fallback
    assertThrows(IllegalArgumentException.class, () -> RowUtil.toJson(row));
  }

  @Test
  void testToJson_Fallback() {
    when(row.size()).thenReturn(1);
    when(row.getColumnName(0)).thenReturn("uuid");
    when(row.getValue(0)).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000000"));

    JsonObject json = RowUtil.toJson(row);
    assertEquals("00000000-0000-0000-0000-000000000000", json.getString("uuid"));
  }

  @Test
  void testFirst() {
    List<Row> rows = List.of(row);
    assertEquals(row, RowUtil.first(rows));
    assertEquals("mapped", RowUtil.first(rows, r -> "mapped"));

    assertThrows(NoSuchElementException.class, () -> RowUtil.first(Collections.emptyList()));
    assertThrows(NoSuchElementException.class, () -> RowUtil.first(null));
    assertThrows(NoSuchElementException.class, () -> RowUtil.first(Collections.emptyList(), r -> "mapped"));
    assertThrows(NoSuchElementException.class, () -> RowUtil.first(null, r -> "mapped"));
  }

  @Test
  void testFirstOrThrow() {
    List<Row> rows = List.of(row);
    assertEquals(row, RowUtil.firstOrThrow(rows, RuntimeException::new));
    assertEquals("mapped", RowUtil.firstOrThrow(rows, r -> "mapped", RuntimeException::new));

    assertThrows(RuntimeException.class, () -> RowUtil.firstOrThrow(Collections.emptyList(), RuntimeException::new));
    assertThrows(RuntimeException.class, () -> RowUtil.firstOrThrow(null, RuntimeException::new));
    assertThrows(RuntimeException.class,
        () -> RowUtil.firstOrThrow(Collections.emptyList(), r -> "mapped", RuntimeException::new));
    assertThrows(RuntimeException.class, () -> RowUtil.firstOrThrow(null, r -> "mapped", RuntimeException::new));
  }

  @Test
  void testFirstOrNull() {
    List<Row> rows = List.of(row);
    assertEquals(row, RowUtil.firstOrNull(rows));
    assertEquals("mapped", RowUtil.firstOrNull(rows, r -> "mapped"));

    assertNull(RowUtil.firstOrNull(Collections.emptyList()));
    assertNull(RowUtil.firstOrNull(null));
    assertNull(RowUtil.firstOrNull(Collections.emptyList(), r -> "mapped"));
    assertNull(RowUtil.firstOrNull((Iterable<Row>) null, r -> "mapped"));
  }

  @Test
  void testMapFirstOrNull_MapFirst() {
    when(row.size()).thenReturn(1);
    when(row.getColumnName(0)).thenReturn("id");
    when(row.getValue(0)).thenReturn(1);
    List<Row> rows = List.of(row);

    Person p1 = RowUtil.mapFirstOrNull(rows, Person.class);
    assertNotNull(p1);
    assertEquals(1, p1.getId());

    Person p2 = RowUtil.mapFirst(rows, Person.class);
    assertNotNull(p2);
    assertEquals(1, p2.getId());

    assertNull(RowUtil.mapFirstOrNull(Collections.emptyList(), Person.class));
    assertThrows(NoSuchElementException.class, () -> RowUtil.mapFirst(Collections.emptyList(), Person.class));
  }

  @Test
  void testMap() {
    List<Row> rows = List.of(row, row);
    List<String> result = RowUtil.map(rows, r -> "test");
    assertEquals(2, result.size());
    assertEquals("test", result.getFirst());

    assertNull(RowUtil.map(null, r -> "test"));
  }

  @Test
  void testStream() {
    List<Row> rows = List.of(row, row);
    Stream<Row> stream = RowUtil.stream(rows);
    assertEquals(2, stream.count());

    Stream<Row> parallelStream = RowUtil.stream(rows, true);
    assertTrue(parallelStream.isParallel());
    assertEquals(2, parallelStream.count());
  }

  @Test
  void testMapTo() {
    when(row.size()).thenReturn(2);
    when(row.getColumnName(0)).thenReturn("id");
    when(row.getColumnName(1)).thenReturn("name");
    when(row.getValue(0)).thenReturn(1);
    when(row.getValue(1)).thenReturn("John");

    Person person = RowUtil.mapTo(row, Person.class);
    assertEquals(1, person.getId());
    assertEquals("John", person.getName());

    List<Person> people = RowUtil.mapTo(List.of(row, row), Person.class);
    assertEquals(2, people.size());
    assertEquals("John", people.getFirst().getName());
  }

  @Test
  void testMapTo_withJsonValues() {
    when(row.size()).thenReturn(3);
    when(row.getColumnName(0)).thenReturn("id");
    when(row.getColumnName(1)).thenReturn("address");
    when(row.getColumnName(2)).thenReturn("hobbies");
    when(row.getValue(0)).thenReturn(1);
    when(row.getValue(1)).thenReturn(JsonObject.of("street", "Main St", "city", "Anytown"));
    when(row.getValue(2)).thenReturn(JsonArray.of("hobby1", "hobby2"));

    Person person = RowUtil.mapTo(row, Person.class);

    assertEquals(1, person.getId());
    assertEquals("Main St", person.getAddress().getStreet());
    assertEquals("Anytown", person.getAddress().getCity());
    assertEquals("hobby1", person.getHobbies().get(0));
    assertEquals("hobby2", person.getHobbies().get(1));
  }

  @Test
  void testMapTo_withNullRow() {
    assertNull(RowUtil.mapTo((Row) null, Person.class));
  }

  @Test
  void testMapTo_withEmptyRow() {
    when(row.size()).thenReturn(0);
    assertNull(RowUtil.mapTo(row, Person.class));
  }

  @Data
  public static class Person {
    private int id;
    private String name;
    private Address address;
    private List<String> hobbies;

    @Data
    public static class Address {
      private String street;
      private String city;
    }
  }
}
