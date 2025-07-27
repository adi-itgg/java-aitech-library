package io.github.adiitgg.vertx.db.orm.module;

import io.github.adiitgg.vertx.db.orm.PreparedQueryFilter;
import io.vertx.sqlclient.PrepareOptions;

public class DeletedAtQueryFilter implements PreparedQueryFilter {
  @Override
  public String filter(String sql, PrepareOptions options) {
    sql = sql.toLowerCase();
    if (sql.contains("select") && sql.contains("m_user")) {
      if (sql.contains("where")) {
        sql = sql.replace("where", "where deleted_at is null and ");
      } else {
        sql += " where deleted_at is null";
      }
    }
    return sql;
  }
}
