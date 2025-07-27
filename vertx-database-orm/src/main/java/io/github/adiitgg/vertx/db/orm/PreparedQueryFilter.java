package io.github.adiitgg.vertx.db.orm;

import io.vertx.sqlclient.PrepareOptions;

public interface PreparedQueryFilter extends PgModule {

  String filter(String sql, PrepareOptions options);

}
