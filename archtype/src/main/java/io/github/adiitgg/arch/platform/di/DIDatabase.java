package io.github.adiitgg.arch.platform.di;

import io.avaje.inject.*;
import io.github.adiitgg.arch.platform.qualifier.ConnectionDbTest;
import io.github.adiitgg.arch.platform.qualifier.DbPrimary;
import io.github.adiitgg.arch.platform.util.TimeUtil;
import io.github.adiitgg.vertx.config.yml.YmlJsonObject;
import io.github.adiitgg.vertx.db.orm.PgRepository;
import io.github.adiitgg.vertx.db.orm.model.PgRepositoryOptions;
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
  @Primary
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
  @Primary
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

  @Bean
  @ConnectionDbTest
  PgConnectOptions providePgConnectTestOptions(@External YmlJsonObject config) {
    val pgCfg = config.getJsonObject("database-test.postgresql");
    val pgConOpt = new PgConnectOptions(pgCfg);

    val pgTrustCfg = pgCfg.getJsonObject("pem-options");
    if (pgTrustCfg != null) {
      pgConOpt.setPemTrustOptions(new PemTrustOptions());
    }
    pgConOpt.setProperties(Map.of("search_path", config.getString("database.schema", "public")));
    return pgConOpt;
  }

  @Bean
  @ConnectionDbTest
  PgRepository providePgRepositoryTest(@External Vertx vertx, @External YmlJsonObject config, @ConnectionDbTest PgConnectOptions pgConnectOptions) {
    OffsetDateTime startTime = OffsetDateTime.now();
    val poolCfg = config.getJsonObject("database-test.postgresql.pool", JsonObject.of());
    val pgPoolOpt = new PoolOptions(poolCfg);

    log.info("initializing database {}... {}:{}", pgConnectOptions.getDatabase(), pgConnectOptions.getHost(), pgConnectOptions.getPort());
    val pool = Pool.pool(vertx, pgConnectOptions, pgPoolOpt);

    val pgRepositoryOptions = PgRepositoryOptions.newBuilder()
      .pool(pool)
      .maxQueryTookTime(config.getLong("database-test.postgresql.max-query-took-time", 2_000L))
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
