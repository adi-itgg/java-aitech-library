package io.aitech.vertx.db.orm.util;

import io.aitech.vertx.db.orm.model.PreparedQuery;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.Objects;


@UtilityClass
public final class RepositoryUtil {

  public static void logQueryEntity(Logger log, PreparedQuery preparedQuery) {
    log.debug("SQL: " + preparedQuery.sql());
    val sb = new StringBuilder();
    for (int i = 0; i < preparedQuery.tuple().size(); i++) {
      if (!sb.isEmpty()) {
        sb.append(" - ");
      }
      sb.append(i).append("=").append(preparedQuery.tuple().getValue(i));
    }
    log.debug("Parameters: " + sb);
  }

  public static <T> Handler<AsyncResult<T>> logQuery(Logger log, Long maxQueryTookTime, long startTime, String sql) {
    val tookTime = System.currentTimeMillis() - startTime;
    final long maxQueryTook = Objects.requireNonNullElse(maxQueryTookTime, 2_000L);
    return ar -> {
      if (tookTime > maxQueryTook) {
        log.warn("Query too slow! " + tookTime + "ms, Maximum time allowed: " + maxQueryTook + "ms. SQL: " + sql);
      }
      if (ar.failed()) {
        val e = ar.cause();
        log.error("Error Query SQL: " + sql + " -> " + e.getMessage());
        log.error("Error from code", e.getCause());
      }
    };
  }

}
