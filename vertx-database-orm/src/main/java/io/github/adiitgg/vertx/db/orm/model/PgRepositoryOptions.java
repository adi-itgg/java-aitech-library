package io.github.adiitgg.vertx.db.orm.model;

import io.vertx.sqlclient.Pool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;


@Getter
@Accessors(fluent = true)
@Builder(builderMethodName = "newBuilder")
@AllArgsConstructor
public class PgRepositoryOptions {

  public static final long DEFAULT_MAX_QUERY_TOOK_TIME = 2000L;

  private Pool pool;
  @Builder.Default
  private Long maxQueryTookTime = DEFAULT_MAX_QUERY_TOOK_TIME;
  private boolean debug;
  private boolean enableModule;

  public PgRepositoryOptions(Pool pool) {
    this.pool = pool;
  }

}
