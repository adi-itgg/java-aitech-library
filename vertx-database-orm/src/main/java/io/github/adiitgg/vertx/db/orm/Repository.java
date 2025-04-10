package io.github.adiitgg.vertx.db.orm;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;

import java.util.List;
import java.util.function.Consumer;

public interface Repository extends SqlClient {

  <T> Future<Row> insert(T entity, boolean returning);
  <T> Future<T> insertEntity(T entity, boolean updateCurrentEntity);
  <T> Future<Row> insertBatch(List<T> entityList, boolean returning);

  <T> Future<Row> upsert(T entity, boolean returning);
  <T> Future<T> upsertEntity(T entity, boolean updateCurrentEntity);
  <T> Future<Row> upsertBatch(List<T> entityList, boolean returning);

  <T> Future<Row> update(T entity, boolean returning);
  <T> Future<T> updateEntity(T entity, boolean updateCurrentEntity);

  <T> Future<Row> update(T entity, Consumer<T> block);

  default <T> Future<Row> insert(T entity) {
    return insert(entity, false);
  }
  default <T> Future<T> insertEntity(T entity) {
    return insertEntity(entity, false);
  }
  default <T> Future<Row> insertBatch(List<T> entityList) {
    return insertBatch(entityList, false);
  }


  default <T> Future<Row> upsert(T entity) {
    return upsert(entity, false);
  }
  default <T> Future<T> upsertEntity(T entity) {
    return upsertEntity(entity, false);
  }
  default <T> Future<Row> upsertBatch(List<T> entityList) {
    return upsertBatch(entityList, false);
  }

  default <T> Future<Row> update(T entity) {
    return update(entity, false);
  }
  default <T> Future<T> updateEntity(T entity) {
    return updateEntity(entity, false);
  }

}
