package io.github.adiitgg.vertx.db.orm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Builder(builderMethodName = "newBuilder")
@Accessors(fluent = true)
@AllArgsConstructor
public class TransactionOptions {

  @Builder.Default
  private Long maxQueryTookTime = PgRepositoryOptions.DEFAULT_MAX_QUERY_TOOK_TIME;
  /**
   * state timeout value: 1ms, 1s, 1min, 1h and 1d
   */
  @Builder.Default
  private String statementTimeout = null; // default "5min"

}
