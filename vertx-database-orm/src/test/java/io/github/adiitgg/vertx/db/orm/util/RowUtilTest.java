package io.github.adiitgg.vertx.db.orm.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import lombok.Data;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RowUtilTest {

  @Mock
  private Row row;


  @Test
  public void testToJsonFirstOrNull_EmptyRows() {
    Iterable<Row> rows = Collections.emptyList();
    JsonObject result = RowUtil.toJsonFirstOrNull(rows);
    assertNull(result);
  }

  @Test
  public void testToJsonFirstOrNull_NullRows() {
    Iterable<Row> rows = null;
    JsonObject result = RowUtil.toJsonFirstOrNull(rows);
    assertNull(result);
  }

  @Test
  public void testToJsonFirstOrNull_HasRow() {
    val rows = new ArrayList<Row>();

    when(row.size()).thenReturn(4);
    when(row.getColumnName(0)).thenReturn("id");
    when(row.getColumnName(1)).thenReturn("address");
    when(row.getColumnName(2)).thenReturn("hobbies");
    when(row.getColumnName(3)).thenReturn("created_at");
    when(row.getValue(0)).thenReturn(1);
    when(row.getValue(1)).thenReturn(JsonObject.of("street", "Main St", "city", "Anytown"));
    when(row.getValue(2)).thenReturn(JsonArray.of("hobby1", "hobby2"));
    when(row.getValue(3)).thenReturn(OffsetDateTime.now());
    rows.add(row);

    JsonObject result = RowUtil.toJsonFirstOrNull(rows);
    assertNotNull(result);
    assertEquals(1, result.getInteger("id"));
    assertEquals("Main St", result.getJsonObject("address").getString("street"));
    assertEquals("Anytown", result.getJsonObject("address").getString("city"));
    assertEquals("hobby1", result.getJsonArray("hobbies").getString(0));
    assertEquals("hobby2", result.getJsonArray("hobbies").getString(1));
  }


  @Test
  public void testMapTo() {
    when(row.size()).thenReturn(2);
    when(row.getColumnName(0)).thenReturn("id");
    when(row.getColumnName(1)).thenReturn("name");
    when(row.getValue(0)).thenReturn(1);
    when(row.getValue(1)).thenReturn("John");

    Person person = RowUtil.mapTo(row, Person.class);

    assertEquals(1, person.getId());
    assertEquals("John", person.getName());
  }

  @Test
  public void testMapTo_withJsonValues() {
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
    assertEquals("hobby1", person.getHobbies().getFirst());
    assertEquals("hobby2", person.getHobbies().get(1));
  }

  @Test
  public void testMapTo_withNullRow() {
    Row row = null;
    Person person = RowUtil.mapTo(row, Person.class);

    assertNull(person);
  }

  @Test
  public void testMapTo_withEmptyRow() {
    when(row.size()).thenReturn(0);

    Person person = RowUtil.mapTo(row, Person.class);

    assertNull(person);
  }

  @Test
  public void testMapToList() {
    List<Row> rows = new ArrayList<>();
    rows.add(row);
    when(row.size()).thenReturn(2);
    when(row.getColumnName(0)).thenReturn("id");
    when(row.getColumnName(1)).thenReturn("name");
    when(row.getValue(0)).thenReturn(1);
    when(row.getValue(1)).thenReturn("John");

    List<Person> people = RowUtil.mapTo(rows, Person.class);

    assertEquals(1, people.size());
    assertEquals(1, people.getFirst().getId());
    assertEquals("John", people.getFirst().getName());
  }

  @Data
  public static class Person {

    private int id;
    private String name;
    private Address address;
    private List<String> hobbies;
    private OffsetDateTime createdAt;

    @Data
    public static class Address {

      private String street;
      private String city;

    }

  }
}
