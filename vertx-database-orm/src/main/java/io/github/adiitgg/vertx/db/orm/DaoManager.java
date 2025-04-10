package io.github.adiitgg.vertx.db.orm;

import io.github.adiitgg.vertx.db.orm.impl.DaoManagerImpl;
import io.github.adiitgg.vertx.db.orm.model.*;
import io.vertx.sqlclient.Row;

import java.util.List;

public interface DaoManager {

  static DaoManager getInstance() {
    return DaoManagerImpl.getInstance();
  }

  void scanEntityPackage(String packageName);

  void prepare(Class<?> entityClass);

  QueryPreparer prepare(Class<?> entityClass, DAOQueryType queryType, List<EntityFieldOptions> updateColumns);

  QueryPreparer getQuery(String key);
  void setQuery(String key, QueryPreparer value);

  EntityOptions getEntityOptions(Class<?> clazz);
  void setEntityOptions(Class<?> clazz, EntityOptions options);

  PreparedQuery getPreparedQueryEntity(Object entity, DAOQueryType queryType, List<EntityFieldOptions> updateColumns, boolean returning);

  PreparedQuery getPreparedQueryEntity(Object entity, DAOQueryType queryType, boolean returning);

  <T> T updateEntityFromRow(PreparedQuery preparedQuery, T entity, Row row);

}
