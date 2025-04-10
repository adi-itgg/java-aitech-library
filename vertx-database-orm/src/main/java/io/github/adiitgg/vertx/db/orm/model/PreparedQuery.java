package io.github.adiitgg.vertx.db.orm.model;

import io.vertx.sqlclient.Tuple;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class PreparedQuery {

  private String sql;
  private Tuple tuple;
  private QueryPreparer queryPreparer;

}
