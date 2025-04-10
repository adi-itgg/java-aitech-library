package io.github.adiitgg.arch.verticle;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.adiitgg.arch.platform.util.TimeUtil;
import io.github.adiitgg.vertx.config.ConfigRetrieverExtended;
import io.github.adiitgg.vertx.config.yml.YmlJsonObject;
import io.avaje.inject.BeanScope;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.jackson.DatabindCodec;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

import static io.vertx.core.Future.await;

@Slf4j
@Getter
@Accessors(fluent = true)
public class MainVerticle extends AbstractVerticle {

  private BeanScope beanScope;
  private final OffsetDateTime startTime = OffsetDateTime.now();
  private final AtomicLong processedRequests = new AtomicLong(0L);
  private final BiFunction<Vertx, YmlJsonObject, BeanScope> beanScopeSupplier;

  public MainVerticle(BiFunction<Vertx, YmlJsonObject, BeanScope> beanScopeSupplier) {
    this.beanScopeSupplier = beanScopeSupplier;
  }

  public MainVerticle() {
    this(null);
  }

  public Future<YmlJsonObject> loadConfig(Vertx vertx, List<String> args) {
    val configRetrieverOptions = new ConfigRetrieverOptions()
      .setIncludeDefaultStores(false)
      .setScanPeriod(-1L);
    val configRetrieverExtended = ConfigRetrieverExtended.create(vertx, configRetrieverOptions)
      .args(args.toArray(new String[0]))
      .format("yml")
      .build();

    log.info("loading config...");
    return configRetrieverExtended.getConfig().map(YmlJsonObject::of).onComplete(t -> {
      log.info("config has been loaded");
      configRetrieverExtended.close();
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  public void start() {
    val startTime = OffsetDateTime.now();

    List<String> args = vertx.getOrCreateContext().processArgs();
    args = args == null ? config().getJsonArray("args", JsonArray.of()).getList() : args;
    log.info("has arguments: {}", String.join(", ", args));


    val config = await(loadConfig(vertx, args));
    config().mergeIn(config);
    System.setProperty("avaje.profiles", config.getString("profile", "development"));

    log.info("service current version is {}", config().getString("commit-id", "unknown"));

    // set default locale & time zone
    Locale.setDefault(Locale.forLanguageTag(config.getString("locale", "in-ID")));
    TimeZone.setDefault(TimeZone.getTimeZone(config.getString("location", "Asia/Jakarta")));

    // setup jackson
    DatabindCodec.mapper()
      .findAndRegisterModules()
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
      .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
      .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
      .setTimeZone(TimeZone.getDefault())
      .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
    ;

    // register shutdown hook
    registerShutdownHook(config.getLong("server.max-await-shutdown", 5L));


    this.beanScope = await(vertx.executeBlocking(() -> {
      if (beanScopeSupplier != null) {
        return beanScopeSupplier.apply(vertx, config);
      }
      return beanScope = BeanScope.builder()
        .bean(Vertx.class, vertx)
        .bean(YmlJsonObject.class, config)
        .build();
    }));

    val deployOptions = new DeploymentOptions()
      .setConfig(config)
      .setInstances(1)
//      .setInstances(VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE)
      .setThreadingModel(ThreadingModel.VIRTUAL_THREAD);
    await(vertx.deployVerticle(() -> beanScope.get(HttpServerVerticle.Factory.class).create(this), deployOptions));

    val took = TimeUtil.measureDynamicTookTime(startTime);
    log.info("initialize to start service took {}", took);
  }

  @Override
  public void stop() {
    if (beanScope != null) {
      log.info("closing Dependency Injection...");
      beanScope.close();
      log.info("Dependency Injection has been closed");
    }
  }

  private void registerShutdownHook(Long maxAwaitShutdown) {
    val thread = Thread.ofVirtual().unstarted(() -> {
      val latch = new CountDownLatch(1);

      log.info("Vert.x is closing...");
      vertx.close()
        .onSuccess(v -> log.info("Vert.x has been successfully closed and will no longer accept new tasks."))
        .onFailure(e -> log.error("Failed to closing Vert.x!", e))
        .onComplete(t -> latch.countDown());

      try {
        if (!latch.await(maxAwaitShutdown, TimeUnit.MINUTES)) {
          log.error("exceeded maximum await shutdown hook!");
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      val totalActiveTime = TimeUtil.measureDynamicTookTime(this.startTime, OffsetDateTime.now());

      log.info("Service has been ended. Total processed requests: {} - Total active time: {}", processedRequests, totalActiveTime);
    });
    Runtime.getRuntime().addShutdownHook(thread);
  }

}
