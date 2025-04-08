package io.aitech.vertx.db.orm.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aitech.vertx.db.orm.DaoManager;
import io.aitech.vertx.db.orm.model.*;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public final class DaoManagerImpl implements DaoManager {

  private final Map<Class<?>, EntityOptions> entityOptions = new HashMap<>();
  private final Map<String, QueryPreparer> queries = new HashMap<>();
  private final AnnotationPersistence annotationPersistence;
  private final ObjectMapper objectMapper;

  public DaoManagerImpl() {
    this.annotationPersistence = new AnnotationPersistence();
    this.objectMapper = DatabindCodec.mapper();
  }

  public static DaoManagerImpl getInstance() {
    return Holder.INSTANCE;
  }

  private static class Holder {
    private static final DaoManagerImpl INSTANCE = new DaoManagerImpl();
  }


  @SneakyThrows
  @Override
  public void scanEntityPackage(String packageName) {
    InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
    if (stream == null) {
      throw new IllegalArgumentException("Package not found: " + packageName);
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    reader.lines()
      .filter(line -> line.endsWith(".class"))
      .map(line -> getClass(line, packageName))
      .filter(Objects::nonNull)
      .forEach(this::prepare);
  }

  private Class<?> getClass(String className, String packageName) {
    try {
      return Class.forName(packageName + "."
        + className.substring(0, className.lastIndexOf('.')));
    } catch (ClassNotFoundException e) {
      // handle the exception
    }
    return null;
  }

  @Override
  public EntityOptions getEntityOptions(Class<?> clazz) {
    EntityOptions options = entityOptions.get(clazz);
    if (options == null) {
      synchronized (entityOptions) {
        options = entityOptions.get(clazz);
      }
    }
    return options;
  }

  @Override
  public void setEntityOptions(Class<?> clazz, EntityOptions options) {
    synchronized (entityOptions) {
      entityOptions.put(clazz, options);
    }
  }

  @Override
  public QueryPreparer getQuery(String key) {
    QueryPreparer query = queries.get(key);
    if (query == null) {
      synchronized (queries) {
        query = queries.get(key);
      }
    }
    return query;
  }

  @Override
  public void setQuery(String key, QueryPreparer value) {
    synchronized (queries) {
      queries.put(key, value);
    }
  }

  private EntityOptions loadClass(Class<?> clazz) {
    EntityOptions currentOptions = getEntityOptions(clazz);
    if (currentOptions != null) {
      return currentOptions;
    }

    EntityOptions options = new EntityOptions()
      .entityClass(clazz)
      .constructor(Arrays.stream(clazz.getDeclaredConstructors()).filter(c -> c.getParameterCount() == 0).findFirst().orElse(null));
    String entityName = annotationPersistence.getEntityName(clazz);
    options.tableName(entityName);

    List<EntityFieldOptions> fieldOptionsList = Arrays.stream(clazz.getDeclaredFields())
      .map(annotationPersistence::createEntityFieldOptions)
      .toList();

    options.entityFieldOptions(fieldOptionsList);

    setEntityOptions(clazz, options);
    return options;
  }

  @Override
  public void prepare(Class<?> entityClass) {
    for (DAOQueryType value : DAOQueryType.VALUES) {
      prepare(entityClass, value, null);
    }
  }

  @Override
  public QueryPreparer prepare(Class<?> entityClass, DAOQueryType queryType, List<EntityFieldOptions> updateColumns) {
    if (entityClass == null) {
      throw new IllegalArgumentException("Entity cannot be null");
    }
    StringBuilder key = new StringBuilder(entityClass.getSimpleName() + "-" + queryType.name());
    if (updateColumns != null && !updateColumns.isEmpty()) {
      key.append("-");
      for (EntityFieldOptions updateColumn : updateColumns) {
        key.append(updateColumn.columnName());
      }
    }
    QueryPreparer current = getQuery(key.toString());
    if (current != null) {
      return current;
    }
    QueryPreparer queryPreparer = switch (queryType) {
      case INSERT -> prepareInsertSQL(entityClass);
      case UPDATE -> prepareUpdateSQL(entityClass, (updateColumns != null && !updateColumns.isEmpty()) ? updateColumns : null);
      case UPSERT -> prepareUpsertSQL(entityClass);
    };
    setQuery(key.toString(), queryPreparer);
    return queryPreparer;
  }


  private QueryPreparer prepareInsertSQL(Class<?> clazz) {
    // Implementation to save data into the database using ORM
    EntityOptions options = loadClass(clazz);
    String tableName = options.tableName();
    // Assuming you have a method to execute SQL queries
    // Execute an INSERT query with the appropriate values from the entity

    List<EntityFieldOptions> params = new ArrayList<>();

    StringBuilder query = new StringBuilder("INSERT INTO ");
    query.append(tableName).append(" (");

    List<EntityFieldOptions> insertableColumns = options.entityFieldOptions().stream()
      .filter(fieldOptions -> !fieldOptions.autoGeneratedId() && fieldOptions.insertable())
      .toList();

    String columns = insertableColumns.stream()
      .map(EntityFieldOptions::columnName)
      .collect(Collectors.joining(", "));

    query.append(columns).append(") VALUES (");
    for (int i = 0; i < insertableColumns.size(); i++) {
      query.append("$").append(i + 1).append(", ");
      params.add(insertableColumns.get(i));
    }
    query.setLength(query.length() - 2);
    query.append(")");

    List<FieldWrapper> updateFields = options.entityFieldOptions().stream()
      .filter(EntityFieldOptions::updateable)
      .map(EntityFieldOptions::fieldWrapper)
      .toList();

    return new QueryPreparer().sql(query.toString()).fieldOptions(params).fieldWrappers(updateFields);
  }

  private QueryPreparer prepareUpsertSQL(Class<?> clazz) {
    // Implementation to save data into the database using ORM
    EntityOptions options = loadClass(clazz);
    String tableName = options.tableName();
    // Assuming you have a method to execute SQL queries
    // Execute an INSERT query with the appropriate values from the entity

    List<EntityFieldOptions> params = new ArrayList<>();

    StringBuilder query = new StringBuilder("INSERT INTO ");
    query.append(tableName).append(" (");

    List<EntityFieldOptions> insertableColumns = options.entityFieldOptions().stream()
      .filter(fieldOptions -> (!fieldOptions.autoGeneratedId() && fieldOptions.insertable()) || fieldOptions.canConflict())
      .toList();

    String columns = insertableColumns.stream()
      .map(EntityFieldOptions::columnName)
      .collect(Collectors.joining(", "));

    query.append(columns).append(") VALUES (");
    for (int i = 0; i < insertableColumns.size(); i++) {
      query.append("$").append(i + 1).append(", ");
      params.add(insertableColumns.get(i));
    }
    query.setLength(query.length() - 2);
    query.append(") ON CONFLICT (");

    List<EntityFieldOptions> canConflictColumns = options.entityFieldOptions().stream()
      .filter(EntityFieldOptions::canConflict)
      .toList();

    final List<EntityFieldOptions> conflictColumns;
    if (canConflictColumns.isEmpty()) {
      conflictColumns = options.entityFieldOptions().stream().filter(EntityFieldOptions::id).toList();
    } else {
      conflictColumns = canConflictColumns;
    }

    final QueryPreparer queryPreparer = new QueryPreparer();
    conflictColumns.forEach(fieldOptions -> query.append(fieldOptions.columnName()).append(", "));
    if (!conflictColumns.isEmpty()) {
      query.setLength(query.length() - 2);
      query.append(")");

      query.append(" DO UPDATE SET ");
      // updateable columns
      List<EntityFieldOptions> updateableColumns = options.entityFieldOptions().stream()
        .filter(fieldOptions -> !fieldOptions.autoGeneratedId() && !fieldOptions.isCreatedAt() && fieldOptions.updateable() && conflictColumns.stream().noneMatch(f -> f.columnName().equals(fieldOptions.columnName())))
        .toList();

      updateableColumns.forEach(fieldOptions -> query.append(fieldOptions.columnName()).append(" = EXCLUDED.").append(fieldOptions.columnName()).append(", "));
      if (!updateableColumns.isEmpty()) {
        query.setLength(query.length() - 2);
      }

      List<FieldWrapper> updateFields = options.entityFieldOptions().stream()
        .filter(EntityFieldOptions::updateable)
        .map(EntityFieldOptions::fieldWrapper)
        .toList();

      queryPreparer.fieldWrappers(updateFields);
    } else {
      query.setLength(query.length() - 2);
      query.append(" DO NOTHING");
      queryPreparer.fieldWrappers(Collections.emptyList());
    }

    return queryPreparer.sql(query.toString()).fieldOptions(params);
  }

  private QueryPreparer prepareUpdateSQL(Class<?> clazz, List<EntityFieldOptions> updateColumns) {
    EntityOptions options = loadClass(clazz);
    String tableName = options.tableName();

    List<EntityFieldOptions> params = new ArrayList<>();

    StringBuilder query = new StringBuilder("UPDATE ")
      .append(tableName)
      .append(" SET ");

    List<EntityFieldOptions> whereColumns = options.entityFieldOptions().stream()
      .filter(EntityFieldOptions::id)
      .toList();

    final List<EntityFieldOptions> updateableColumns = new ArrayList<>();
    if (updateColumns != null) {
      List<EntityFieldOptions> updateCols = updateColumns.stream()
        .filter(fieldOptions -> fieldOptions.isUpdatedAt() || (fieldOptions.updateable() && whereColumns.stream().noneMatch(f -> f.columnName().equals(fieldOptions.columnName()))))
        .toList();
      updateableColumns.addAll(updateCols);
      List<EntityFieldOptions> updatedFields = options.entityFieldOptions().stream()
        .filter(fieldOptions -> fieldOptions.isUpdatedAt() && updateCols.stream().noneMatch(fo -> fo.columnName().equals(fieldOptions.columnName())))
        .toList();
      updateableColumns.addAll(updatedFields);
    } else {
      List<EntityFieldOptions> updateCols = options.entityFieldOptions().stream()
        .filter(fieldOptions -> fieldOptions.isUpdatedAt() || (!fieldOptions.autoGeneratedId() && fieldOptions.updateable() && whereColumns.stream().noneMatch(f -> f.columnName().equals(fieldOptions.columnName()))))
        .toList();
      updateableColumns.addAll(updateCols);
    }

    for (EntityFieldOptions fieldOption : updateableColumns) {
      query.append(fieldOption.columnName()).append(" = $").append(updateableColumns.indexOf(fieldOption) + 1).append(", ");
      params.add(fieldOption);
    }
    query.setLength(query.length() - 2);

    query.append(" WHERE ");
    whereColumns.forEach(f -> {
      query.append(f.columnName()).append(" = $").append((updateableColumns.size() + whereColumns.indexOf(f)) + 1).append(" AND ");
      params.add(f);
    });
    query.setLength(query.length() - 4);


    List<FieldWrapper> updateFields = options.entityFieldOptions().stream()
      .filter(EntityFieldOptions::updateable)
      .map(EntityFieldOptions::fieldWrapper)
      .toList();

    return new QueryPreparer().sql(query.toString()).fieldOptions(params).fieldWrappers(updateFields);
  }

  @Override
  public PreparedQuery getPreparedQueryEntity(Object entity, DAOQueryType queryType, boolean returning) {
    return getPreparedQueryEntity(entity, queryType, null, returning);
  }

  @Override
  public PreparedQuery getPreparedQueryEntity(Object entity, DAOQueryType queryType, List<EntityFieldOptions> updateColumns, boolean returning) {
    final QueryPreparer queryPreparer = prepare(entity.getClass(), queryType, updateColumns);
    final Object[] params = queryPreparer.fieldOptions().stream().map(f -> {
      Object result = f.fieldWrapper().getter().apply(entity);

      boolean isInsert = queryType == DAOQueryType.INSERT || queryType == DAOQueryType.UPSERT;
      if (isInsert && result == null && (f.isCreatedAt() || f.isUpdatedAt())) {
        return f.defaultValueProvider().get();
      }
      if (f.isUpdatedAt()) {
        return f.defaultValueProvider().get();
      }
      if (f.serialized() != null) {
        return objectMapper.convertValue(result, f.serialized());
      }

      return result;
    }).toArray();
    return new PreparedQuery()
      .sql(queryPreparer.sql() + (returning ? " RETURNING *" : ""))
      .tuple(Tuple.wrap(params))
      .queryPreparer(queryPreparer);
  }

  @Override
  public <T> T updateEntityFromRow(PreparedQuery preparedQuery, T entity, Row row) {
    if (entity == null) {
      return null;
    }
    for (FieldWrapper fieldWrapper : preparedQuery.queryPreparer().fieldWrappers()) {
      fieldWrapper.setter().accept(entity, row.getValue(fieldWrapper.columnName()));
    }
    return entity;
  }


}
