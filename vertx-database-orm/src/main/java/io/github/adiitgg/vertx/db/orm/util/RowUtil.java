package io.github.adiitgg.vertx.db.orm.util;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.sqlclient.Row;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static io.vertx.sqlclient.Tuple.JSON_NULL;


@UtilityClass
public final class RowUtil {


  public static JsonObject toJsonFirstOrNull(Iterable<Row> rows) {
    return toJsonFirstOrNull(rows, true);
  }

  public static JsonObject toJsonFirstOrNull(Iterable<Row> rows, boolean camelCase) {
    return (rows != null && rows.iterator().hasNext()) ? toJsonFirst(rows, camelCase) : null;
  }

  public static JsonObject toJsonFirst(Iterable<Row> rows) {
    return toJsonFirst(rows, true);
  }

  public static JsonObject toJsonFirst(Iterable<Row> rows, boolean camelCase) {
    Iterator<Row> iterator = rows == null ? null : rows.iterator();
    if (rows == null || !iterator.hasNext()) {
      throw new NoSuchElementException("rows is empty");
    }
    return toJson(iterator.next(), camelCase);
  }

  public static JsonArray toJson(Iterable<Row> rows) {
    return toJson(rows, true);
  }

  public static JsonArray toJson(Iterable<Row> rows, boolean camelCase) {
    JsonArray jsonArray = new JsonArray();
    for (Row row : rows) {
      JsonObject json = toJson(row, camelCase);
      jsonArray.add(json);
    }
    return jsonArray;
  }

  public static JsonObject toJson(Row row) {
    return toJson(row, true);
  }

  public static JsonObject toJson(Row row, boolean camelCase) {
    JsonObject json = new JsonObject();
    int colSize = row.size();

    for (int pos = 0; pos < colSize; ++pos) {
      String name = camelCase ? Utils.snakeToCamel(row.getColumnName(pos)) : row.getColumnName(pos);
      Object value = row.getValue(pos);
      json.put(name, toJson(value));
    }
    return json;
  }

  private static Object toJson(Object value) {
    if (value == null || value == JSON_NULL) {
      return null;
    }

    if (value instanceof String
      || value instanceof Boolean
      || value instanceof Number
      || value instanceof Buffer
      || value instanceof JsonObject
      || value instanceof JsonArray
    ) {
      return value;
    }

    if (value.getClass().isArray()) {
      int len = Array.getLength(value);
      JsonArray array = new JsonArray(new ArrayList<>(len));
      for (int idx = 0; idx < len; idx++) {
        Object component = toJson(Array.get(value, idx));
        array.add(component);
      }
      return array;
    }

    if (value instanceof Temporal temporal) {
      if (temporal.isSupported(ChronoField.INSTANT_SECONDS)) {
        return DateTimeFormatter.ISO_INSTANT.format(temporal);
      }
    }

    return DatabindCodec.mapper().convertValue(value, String.class);
  }

  public static <T> T first(Iterable<Row> rows, Function<Row, T> mapper) {
    Iterator<Row> iterator = rows == null ? null : rows.iterator();
    if (rows == null || !iterator.hasNext()) {
      throw new NoSuchElementException("rows is null or empty");
    }
    return mapper.apply(iterator.next());
  }

  public static Row first(Iterable<Row> rows) {
    Iterator<Row> iterator = rows == null ? null : rows.iterator();
    if (rows == null || !iterator.hasNext()) {
      throw new NoSuchElementException("rows is null or empty");
    }
    return iterator.next();
  }

  @SneakyThrows
  public static Row firstOrThrow(Iterable<Row> rows, Supplier<Throwable> exception) {
    Iterator<Row> iterator = rows == null ? null : rows.iterator();
    if (rows == null || !iterator.hasNext()) {
      throw exception.get();
    }
    return iterator.next();
  }

  @SneakyThrows
  public static <T> T firstOrThrow(Iterable<Row> rows, Function<Row, T> mapper, Supplier<Throwable> exception) {
    Iterator<Row> iterator = rows == null ? null : rows.iterator();
    if (rows == null || !iterator.hasNext()) {
      throw exception.get();
    }
    return mapper.apply(iterator.next());
  }

  public static <T> T firstOrNull(Iterable<Row> rows, Function<Row, T> mapper) {
    Row row = firstOrNull(rows);
    return row == null ? null : mapper.apply(row);
  }

  public static Row firstOrNull(Iterable<Row> rows) {
    Iterator<Row> iterator = rows == null ? null : rows.iterator();
    return (rows == null || !iterator.hasNext()) ? null : iterator.next();
  }

  public static <T> T mapFirstOrNull(Iterable<Row> rows, Class<T> classType) {
    return firstOrNull(rows, row -> mapTo(row, classType));
  }

  public static <T> T mapFirst(Iterable<Row> rows, Class<T> classType) {
    return first(rows, row -> mapTo(row, classType));
  }

  public static <T> List<T> map(Iterable<Row> rows, Function<Row, T> mapper) {
    if (rows == null) {
      return null;
    }
    List<T> data = new ArrayList<>();
    for (Row row : rows) {
      data.add(mapper.apply(row));
    }
    return Collections.unmodifiableList(data);
  }

  public static Stream<Row> stream(Iterable<Row> rows) {
    return stream(rows, false);
  }

  public static Stream<Row> stream(Iterable<Row> rows, boolean parallel) {
    return StreamSupport.stream(rows.spliterator(), parallel);
  }

  public static <T> List<T> mapTo(Iterable<Row> rows, Class<T> targetClass) {
    List<T> result = new ArrayList<>();
    for (Row row : rows) {
      result.add(mapTo(row, targetClass));
    }
    return result;
  }

  public static <T> T mapTo(Row row, Class<T> target) {
    if (row == null || row.size() == 0) {
      return null;
    }

    // TODO improve using LambdaMetaFactory but support jackson annotation maybe?
    /*final DaoManager daoManager = DaoManager.getInstance();
    final EntityOptions entityOptions = daoManager.getEntityOptions(target);
    if (entityOptions != null && entityOptions.constructor() != null) {
      Object instance = entityOptions.constructor().newInstance();
      for (EntityFieldOptions entityFieldOption : entityOptions.entityFieldOptions()) {
        int pos = row.getColumnIndex(entityFieldOption.columnName());
        if (pos == -1) {
          continue;
        }
        final FieldWrapper fieldWrapper = entityFieldOption.fieldWrapper();
        Object value = row.getValue(entityFieldOption.columnName());
        if (value != null && (value.getClass().isAssignableFrom(JsonObject.class) || value.getClass().isAssignableFrom(JsonArray.class))) {
          fieldWrapper.setter().accept(instance, DatabindCodec.mapper().convertValue(value, fieldWrapper.type()));
          continue;
        }
        fieldWrapper.setter().accept(instance, value);
      }
      return (T) instance;
    }*/

    final JsonObject jsonObject = new JsonObject();
    for (int i = 0; i < row.size(); i++) {
      String key = row.getColumnName(i);
      Object value = row.getValue(i);
      if (value instanceof JsonObject o) {
        value = o.getMap();
      } else if (value instanceof JsonArray a) {
        value = a.getList();
      }
      jsonObject.put(key, value);
      jsonObject.put(Utils.snakeToCamel(key), value);
    }
    return jsonObject.mapTo(target);
  }

}
