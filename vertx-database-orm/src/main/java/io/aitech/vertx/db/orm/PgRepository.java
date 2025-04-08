package io.aitech.vertx.db.orm;

import io.aitech.vertx.db.orm.impl.PgRepositoryImpl;
import io.aitech.vertx.db.orm.model.PgRepositoryOptions;
import io.aitech.vertx.db.orm.model.TransactionOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.impl.CloseFuture;
import io.vertx.core.impl.VertxInternal;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnectOptions;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.impl.PoolBase;
import io.vertx.sqlclient.impl.SqlClientInternal;
import io.vertx.sqlclient.spi.Driver;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.reflect.Field;
import java.util.ServiceConfigurationError;
import java.util.function.Consumer;
import java.util.function.Function;

public interface PgRepository extends Pool, SqlClientInternal, Repository {

  /**
   * Like {@link #create(SqlConnectOptions, PoolOptions)} with default options.
   */
  static Pool create(SqlConnectOptions connectOptions) {
    return create(connectOptions, new PoolOptions());
  }

  /**
   * Like {@link #create(Vertx, SqlConnectOptions, PoolOptions)} with a Vert.x instance created automatically.
   */
  static Pool create(SqlConnectOptions database, PoolOptions options) {
    return create(null, database, options);
  }

  /**
   * Create a connection pool to the {@code database} with the given {@code options}.
   *
   * <p> A {@link Driver} will be selected among the drivers found on the classpath returning
   * {@code true} when {@link Driver#acceptsOptions(SqlConnectOptions)} applied to the first options
   * of the list.
   *
   * @param vertx    the Vertx instance to be used with the connection pool
   * @param database the options used to create the connection pool, such as database hostname
   * @return the connection pool
   * @throws ServiceConfigurationError if no compatible drivers are found, or if multiple compatible drivers are found
   */
  static PgRepository create(Vertx vertx, SqlConnectOptions database) {
    val pool = Pool.pool(vertx, database, new PoolOptions());
    val pgRepositoryOptions = PgRepositoryOptions.newBuilder()
      .pool(pool)
      .build();
    return create(pgRepositoryOptions);
  }

  /**
   * Create a connection pool to the {@code database} with the given {@code options}.
   *
   * <p> A {@link Driver} will be selected among the drivers found on the classpath returning
   * {@code true} when {@link Driver#acceptsOptions(SqlConnectOptions)} applied to the first options
   * of the list.
   *
   * @param vertx    the Vertx instance to be used with the connection pool
   * @param database the options used to create the connection pool, such as database hostname
   * @param options  the options for creating the pool
   * @return the connection pool
   * @throws ServiceConfigurationError if no compatible drivers are found, or if multiple compatible drivers are found
   */
  static PgRepository create(Vertx vertx, SqlConnectOptions database, PoolOptions options) {
    val pool = Pool.pool(vertx, database, options);
    val pgRepositoryOptions = PgRepositoryOptions.newBuilder()
      .pool(pool)
      .build();
    return create(pgRepositoryOptions);
  }

  /**
   * Create a repository from the given pool.
   *
   * @param pgRepositoryOptions the pool instance to be used
   * @return the repository
   */
  @SneakyThrows
  static PgRepository create(PgRepositoryOptions pgRepositoryOptions) {
    final Pool pool = pgRepositoryOptions.pool();
    if (pool == null) {
      throw new IllegalArgumentException("Pool cannot be null");
    }
    Class<?> poolBaseClass = PoolBase.class;

    Field vertxField = poolBaseClass.getDeclaredField("vertx");
    if (!vertxField.trySetAccessible()) {
      throw new ServiceConfigurationError("Cannot access vertx field");
    }
    Field closeFutureField = poolBaseClass.getDeclaredField("closeFuture");
    if (!closeFutureField.trySetAccessible()) {
      throw new ServiceConfigurationError("Cannot access close future field");
    }
    Field delegateField = poolBaseClass.getDeclaredField("delegate");
    if (!delegateField.trySetAccessible()) {
      throw new ServiceConfigurationError("Cannot access delegate field");
    }

    VertxInternal vertx = (VertxInternal) vertxField.get(pool);
    CloseFuture closeFuture = (CloseFuture) closeFutureField.get(pool);
    Pool delegate = (Pool) delegateField.get(pool);

    DaoManager daoManager = DaoManager.getInstance();

    return new PgRepositoryImpl(pgRepositoryOptions, vertx, closeFuture, delegate, daoManager);
  }

  Future<Void> checkConnection();

  <T> Future<T> transaction(Function<TransactionRepository, Future<T>> block);

  <T> Future<T> transaction(TransactionOptions options, Function<TransactionRepository, Future<T>> block);

  default Future<Void> transaction(Consumer<TransactionRepository> block) {
    Function<TransactionRepository, Future<Void>> action = repository -> {
      try {
        block.accept(repository);
      } catch (Throwable e) {
        return Future.failedFuture(e);
      }
      return Future.succeededFuture();
    };
    return transaction(action);
  }

  default Future<Void> withTransaction(Consumer<SqlConnection> block) {
    return Pool.super.withTransaction(t -> {
      try {
        block.accept(t);
      } catch (Throwable e) {
        return Future.failedFuture(e);
      }
      return Future.succeededFuture();
    });
  }

}
