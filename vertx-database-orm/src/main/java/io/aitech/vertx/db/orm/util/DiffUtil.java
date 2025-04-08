package io.aitech.vertx.db.orm.util;

import io.aitech.vertx.db.orm.DaoManager;
import io.aitech.vertx.db.orm.model.diff.DiffSnapshot;
import io.aitech.vertx.db.orm.model.EntityFieldOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.val;

import java.util.*;

public final class DiffUtil {

  public static DiffSnapshot snapshot(Object entity) {
    if (entity == null) {
      throw new IllegalArgumentException("entity is null");
    }
    val options = DaoManager.getInstance().getEntityOptions(entity.getClass());
    val entityValues = new ArrayList<>(options.entityFieldOptions().size());
    for (EntityFieldOptions entityFieldOption : options.entityFieldOptions()) {
      entityValues.add(wrap(entityFieldOption.fieldWrapper().getter().apply(entity)));
    }
    return new DiffSnapshot()
      .entityOptions(options)
      .values(entityValues);
  }

  public static List<EntityFieldOptions> diff(DiffSnapshot diffSnapshot, Object newEntity) {
    if (diffSnapshot == null) {
      throw new IllegalArgumentException("snapshot is null");
    }
    if (newEntity == null) {
      throw new IllegalArgumentException("entity is null");
    }

    val options = DaoManager.getInstance().getEntityOptions(newEntity.getClass());
    val diffProperty = new ArrayList<EntityFieldOptions>();

    int index = 0;
    for (EntityFieldOptions entityFieldOption : options.entityFieldOptions()) {
      val oldValue = diffSnapshot.values().get(index);
      val newValue = wrap(entityFieldOption.fieldWrapper().getter().apply(newEntity));

      if (!Objects.equals(oldValue, newValue)) {
        diffProperty.add(entityFieldOption);
      }

      index++;
    }
    return diffProperty;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static Object wrap(Object value) {
    if (value instanceof JsonObject o) {
      val newObject = new JsonObject();
      for (Map.Entry<String, Object> stringObjectEntry : o) {
        newObject.put(stringObjectEntry.getKey(), wrap(stringObjectEntry.getValue()));
      }
      return newObject;
    }
    if (value instanceof Map<?, ?> map) {
      val newMap = new LinkedHashMap<>();
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        newMap.put(entry.getKey(), wrap(entry.getValue()));
      }
      return newMap;
    }
    if (value instanceof JsonArray ar) {
      val newArray = new JsonArray();
      for (Object o : ar) {
        newArray.add(wrap(o));
      }
      return newArray;
    }
    if (value instanceof List list) {
      return new ArrayList<>(list);
    }
    return value;
  }

}
