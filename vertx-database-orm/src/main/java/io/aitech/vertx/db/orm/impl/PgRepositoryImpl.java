package io.aitech.vertx.db.orm.impl;

import io.aitech.vertx.db.orm.DaoManager;
import io.aitech.vertx.db.orm.PgRepository;
import io.aitech.vertx.db.orm.TransactionRepository;
import io.aitech.vertx.db.orm.util.DiffUtil;
import io.aitech.vertx.db.orm.model.DAOQueryType;
import io.aitech.vertx.db.orm.model.PgRepositoryOptions;
import io.aitech.vertx.db.orm.model.PreparedQuery;
import io.aitech.vertx.db.orm.model.TransactionOptions;
import io.aitech.vertx.db.orm.util.RepositoryUtil;
import io.aitech.vertx.db.orm.util.RowUtil;
import io.vertx.core.Future;
import io.vertx.core.impl.CloseFuture;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.sqlclient.*;
import io.vertx.sqlclient.impl.PoolBase;
import io.vertx.sqlclient.impl.PoolImpl;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.aitech.vertx.db.orm.util.RepositoryUtil.logQuery;

public class PgRepositoryImpl extends PoolBase<PoolImpl> implements PgRepository {

  private static final Logger log = LoggerFactory.getLogger(PgRepository.class);

  private final PgRepositoryOptions pgRepositoryOptions;
  private final DaoManager daoManager;

  public PgRepositoryImpl(PgRepositoryOptions pgRepositoryOptions, VertxInternal vertx, CloseFuture closeFuture, Pool delegate, DaoManager daoManager) {
    super(vertx, closeFuture, delegate);
    this.pgRepositoryOptions = pgRepositoryOptions;
    this.daoManager = daoManager;
  }


  private <T> Future<Row> executePreparedQuery(T entity, DAOQueryType queryType, boolean returning) {
    final PreparedQuery preparedQuery = daoManager.getPreparedQueryEntity(entity, queryType, returning);
    if (pgRepositoryOptions.debug()) {
      RepositoryUtil.logQueryEntity(log, preparedQuery);
    }
    final long startTime = System.currentTimeMillis();
    return preparedQuery(preparedQuery.sql())
      .execute(preparedQuery.tuple())
      .onComplete(logQuery(log, pgRepositoryOptions.maxQueryTookTime(), startTime, preparedQuery.sql()))
      .map(RowUtil::firstOrNull);
  }

  private <T> Future<T> executeAndUpdatePreparedQuery(T entity, DAOQueryType queryType, boolean updateCurrentEntity) {
    final PreparedQuery preparedQuery = daoManager.getPreparedQueryEntity(entity, queryType, updateCurrentEntity);
    if (pgRepositoryOptions.debug()) {
      RepositoryUtil.logQueryEntity(log, preparedQuery);
    }
    final long startTime = System.currentTimeMillis();
    return preparedQuery(preparedQuery.sql())
      .execute(preparedQuery.tuple())
      .onComplete(logQuery(log, pgRepositoryOptions.maxQueryTookTime(), startTime, preparedQuery.sql()))
      .map(rows -> {
        if (updateCurrentEntity) {
          return daoManager.updateEntityFromRow(preparedQuery, entity, RowUtil.firstOrNull(rows));
        }
        return entity;
      });
  }

  private <T> Future<Row> executeBatchPreparedQuery(List<T> entityList, DAOQueryType queryType, boolean returning) {
    if (entityList.isEmpty()) {
      throw new IllegalArgumentException("entityList must not be empty");
    }
    PreparedQuery preparedQuery = null;
    final List<Tuple> tuples = new ArrayList<>();
    for (T entity : entityList) {
      final PreparedQuery pQuery = daoManager.getPreparedQueryEntity(entity, queryType, returning);
      if (preparedQuery == null) {
        preparedQuery = pQuery;
      }
      tuples.add(pQuery.tuple());
    }
    if (pgRepositoryOptions.debug()) {
      RepositoryUtil.logQueryEntity(log, preparedQuery);
    }
    final long startTime = System.currentTimeMillis();
    return preparedQuery(preparedQuery.sql())
      .executeBatch(tuples)
      .onComplete(logQuery(log, pgRepositoryOptions.maxQueryTookTime(), startTime, preparedQuery.sql()))
      .map(RowUtil::firstOrNull);
  }

  @Override
  public <T> Future<Row> insert(T entity, boolean returning) {
    return executePreparedQuery(entity, DAOQueryType.INSERT, returning);
  }

  @Override
  public <T> Future<T> insertEntity(T entity, boolean updateCurrentEntity) {
    return executeAndUpdatePreparedQuery(entity, DAOQueryType.INSERT, updateCurrentEntity);
  }

  @Override
  public <T> Future<Row> insertBatch(List<T> entityList, boolean returning) {
    return executeBatchPreparedQuery(entityList, DAOQueryType.INSERT, returning);
  }

  @Override
  public <T> Future<Row> upsert(T entity, boolean returning) {
    return executePreparedQuery(entity, DAOQueryType.UPSERT, returning);
  }

  @Override
  public <T> Future<T> upsertEntity(T entity, boolean updateCurrentEntity) {
    return executeAndUpdatePreparedQuery(entity, DAOQueryType.UPSERT, updateCurrentEntity);
  }

  @Override
  public <T> Future<Row> upsertBatch(List<T> entityList, boolean returning) {
    return executeBatchPreparedQuery(entityList, DAOQueryType.UPSERT, returning);
  }

  @Override
  public <T> Future<Row> update(T entity, boolean returning) {
    return executePreparedQuery(entity, DAOQueryType.UPDATE, returning);
  }

  @Override
  public <T> Future<T> updateEntity(T entity, boolean updateCurrentEntity) {
    return executeAndUpdatePreparedQuery(entity, DAOQueryType.UPDATE, updateCurrentEntity);
  }

  @Override
  public <T> Future<Row> update(T entity, Consumer<T> block) {
    val commit = DiffUtil.snapshot(entity);
    block.accept(entity);
    val diffProperty = DiffUtil.diff(commit, entity);
    val preparedQuery = daoManager.getPreparedQueryEntity(entity, DAOQueryType.UPDATE, diffProperty, false);
    val tuple = preparedQuery.tuple();

    if (pgRepositoryOptions.debug()) {
      RepositoryUtil.logQueryEntity(log, preparedQuery);
    }
    final long startTime = System.currentTimeMillis();
    return preparedQuery(preparedQuery.sql())
      .execute(tuple)
      .onComplete(logQuery(log, pgRepositoryOptions.maxQueryTookTime(), startTime, preparedQuery.sql()))
      .map(RowUtil::firstOrNull);
  }

  private Future<Void> loadConfigTransaction(TransactionOptions options, SqlConnection connection) {
    if (options.statementTimeout() == null) {
      return Future.succeededFuture();
    }
    return connection.preparedQuery("SET LOCAL statement_timeout = '" + options.statementTimeout() + "'")
      .execute()
      .mapEmpty();
  }

  @Override
  public Future<Void> checkConnection() {
    return preparedQuery("SELECT 'ok' as ok")
      .execute()
      .map(rows -> {
        boolean succeed = rows.size() == 1;
        if (!succeed) {
          throw new IllegalArgumentException("Connection failed");
        }
        return null;
      });
  }

  @Override
  public <T> Future<T> transaction(Function<TransactionRepository, Future<T>> block) {
    TransactionOptions options = TransactionOptions.newBuilder().maxQueryTookTime(pgRepositoryOptions.maxQueryTookTime()).build();
    return transaction(options, block);
  }

  @Override
  public <T> Future<T> transaction(TransactionOptions options, Function<TransactionRepository, Future<T>> block) {
    return getConnection().flatMap(conn -> conn
      .begin()
      .flatMap(tx -> loadConfigTransaction(options, conn).map(u -> tx))
      .flatMap(tx -> {
        var repo = new TransactionRepositoryImpl(options, conn, tx, daoManager);
        try {
          return block.apply(repo).compose(res -> repo.commitTransaction()
            .flatMap(v -> Future.succeededFuture(res)), err -> {
            if (err instanceof TransactionRollbackException) {
              return Future.failedFuture(err);
            } else {
              return repo.getTransaction() == null ? Future.failedFuture(err) : tx.rollback().compose(v -> Future.failedFuture(err), failure -> Future.failedFuture(err));
            }
          });
        } catch (Throwable e) {
          return Future.failedFuture(e);
        }
      })
      .onComplete(ar -> conn.close()));
  }
}
