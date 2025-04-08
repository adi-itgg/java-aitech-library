package io.aitech.arch.platform.di;

import io.aitech.arch.platform.util.TimeUtil;
import io.aitech.vertx.config.yml.YmlJsonObject;
import io.aitech.vertx.db.orm.PgRepository;
import io.aitech.vertx.db.orm.model.PgRepositoryOptions;
import io.avaje.inject.Bean;
import io.avaje.inject.External;
import io.avaje.inject.Factory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.time.OffsetDateTime;
import java.util.Map;

@Slf4j
@Factory
public final class DIDatabase {

  @Bean
  PgConnectOptions providePgConnectOptions(@External YmlJsonObject config) {
    val pgCfg = config.getJsonObject("database.postgresql");
    val pgConOpt = new PgConnectOptions(pgCfg);

    val pgTrustCfg = pgCfg.getJsonObject("pem-options");
    if (pgTrustCfg != null) {
      pgConOpt.setPemTrustOptions(new PemTrustOptions());
    }
    pgConOpt.setProperties(Map.of("search_path", config.getString("database.schema", "public")));
    return pgConOpt;
  }

  @Bean
  PgRepository providePgRepository(@External Vertx vertx, @External YmlJsonObject config, PgConnectOptions pgConnectOptions) {
    OffsetDateTime startTime = OffsetDateTime.now();
    val poolCfg = config.getJsonObject("database.postgresql.pool", JsonObject.of());
    val pgPoolOpt = new PoolOptions(poolCfg);

    log.info("initializing database {}... {}:{}", pgConnectOptions.getDatabase(), pgConnectOptions.getHost(), pgConnectOptions.getPort());
    val pool = Pool.pool(vertx, pgConnectOptions, pgPoolOpt);

    val pgRepositoryOptions = PgRepositoryOptions.newBuilder()
      .pool(pool)
      .maxQueryTookTime(config.getLong("database.postgresql.max-query-took-time", 2_000L))
      .build();


    val pgRepository = PgRepository.create(pgRepositoryOptions);

    pgRepository.checkConnection()
      .onSuccess(con -> log.info("successfully connected to database {}. took {}", pgConnectOptions.getDatabase(), TimeUtil.measureDynamicTookTime(startTime)))
      .onFailure(e -> {
        log.error("failed to connect to database {}.", pgConnectOptions.getDatabase(), e);
        vertx.close();
      });

    return pgRepository;
  }

}
