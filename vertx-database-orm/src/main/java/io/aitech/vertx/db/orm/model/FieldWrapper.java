package io.aitech.vertx.db.orm.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Data
@Accessors(fluent = true)
public class FieldWrapper {

  private String name;
  private String columnName;
  private BiConsumer<Object, Object> setter;
  private Function<Object, Object> getter;
  private Class<?> type;

}
