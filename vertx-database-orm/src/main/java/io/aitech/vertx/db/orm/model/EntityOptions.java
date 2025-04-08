package io.aitech.vertx.db.orm.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Constructor;
import java.util.List;

@Data
@Accessors(fluent = true)
public class EntityOptions {

  private String tableName;
  private List<EntityFieldOptions> entityFieldOptions;
  private Class<?> entityClass;
  private Constructor<?> constructor;

}
