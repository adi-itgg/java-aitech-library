package io.aitech.vertx.db.orm;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxImpl;
import io.vertx.junit5.VertxExtension;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.UUID;

@Slf4j
@Accessors(fluent = true)
@ExtendWith(VertxExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseEmbeddedTest {

  protected EmbeddedPostgres epg;
  protected VertxImpl vertxImpl;
  private DatabaseOptions databaseOptions;

  private final File workDirectory = new File("embedded-postgres-workdir");
  private final File dataDirectory = new File("embedded-postgres");

  protected <T> T await(Future<T> future) {
    return future.toCompletionStage().toCompletableFuture().join();
  }

  @BeforeAll
  protected void setup(Vertx vertx) {
    this.vertxImpl = (VertxImpl) vertx;
    setupDatabase();
    onStart(vertx, this.databaseOptions);
  }

  protected void onStart(Vertx vertx, DatabaseOptions databaseOptions) {

  }

  private void cleanupFiles() {
    try {
      FileUtils.deleteDirectory(dataDirectory);
    } catch (IOException var3) {
      log.error("Could not clean up data directory {}", dataDirectory.getAbsolutePath(), var3);
    }
    try {
      FileUtils.deleteDirectory(workDirectory);
    } catch (IOException var3) {
      log.error("Could not clean up working directory {}", workDirectory.getAbsolutePath(), var3);
    }
  }

  @SneakyThrows
  private void setupDatabase() {
    cleanupFiles();
    FileUtils.createParentDirectories(new File(workDirectory, "embedded-postgres"));
    FileUtils.createParentDirectories(new File(dataDirectory, "embedded-postgres"));

    this.databaseOptions = new DatabaseOptions()
      .port(2345)
      .database("test")
      .user("unit-test")
      .password(UUID.randomUUID().toString().replace("-", ""));

    this.epg = EmbeddedPostgres.builder()
      .setPort(this.databaseOptions.port())
      .setCleanDataDirectory(true)
      .setDataDirectory(dataDirectory)
      .setOverrideWorkingDirectory(workDirectory)
      .start();

    vertxImpl.addCloseHook(p -> {
      try {
        this.epg.close();
      } catch (IOException e) {
        log.error("Could not close embedded postgres", e);
      }
      cleanupFiles();
      p.tryComplete();
    });

    @Cleanup val dbConnection = this.epg.getPostgresDatabase().getConnection();
    directQuerySQL(dbConnection, "CREATE DATABASE \"" + this.databaseOptions.database() + "\";");
    directQuerySQL(dbConnection, "CREATE ROLE \"unit-test\" WITH LOGIN PASSWORD '" + this.databaseOptions.password() + " ';");
    directQuerySQL(dbConnection, "GRANT ALL PRIVILEGES ON DATABASE " + this.databaseOptions.database() + " TO \"" + this.databaseOptions.user() + "\";");
  }

  @SneakyThrows
  private void directQuerySQL(Connection connection, String sql) {
    @Cleanup val statement = connection.createStatement();
    statement.executeUpdate(sql);
  }

  @Data
  @Accessors(fluent = true)
  public static class DatabaseOptions {

    private int port;
    private String database;
    private String user;
    private String password;

  }

}
