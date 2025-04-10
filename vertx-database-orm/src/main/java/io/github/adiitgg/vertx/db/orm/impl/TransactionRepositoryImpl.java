package io.github.adiitgg.vertx.db.orm.impl;


import io.github.adiitgg.vertx.db.orm.DaoManager;
import io.github.adiitgg.vertx.db.orm.TransactionRepository;
import io.github.adiitgg.vertx.db.orm.util.DiffUtil;
import io.github.adiitgg.vertx.db.orm.model.DAOQueryType;
import io.github.adiitgg.vertx.db.orm.model.PreparedQuery;
import io.github.adiitgg.vertx.db.orm.model.TransactionOptions;
import io.github.adiitgg.vertx.db.orm.util.RowUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.sqlclient.*;
import io.vertx.sqlclient.spi.DatabaseMetadata;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static io.github.adiitgg.vertx.db.orm.util.RepositoryUtil.logQuery;

@SuppressWarnings("SqlSourceToSinkFlow")
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

  private static final Logger log = LoggerFactory.getLogger(TransactionRepository.class);

  private final TransactionOptions options;
  private final SqlConnection sqlConnection;
  private final Transaction transaction;
  private final DaoManager daoManager;
  private boolean commited;


  private <T> Future<Row> executePreparedQuery(T entity, DAOQueryType queryType, boolean returning) {
    final PreparedQuery preparedQuery = daoManager.getPreparedQueryEntity(entity, queryType, returning);
    final long startTime = System.currentTimeMillis();
    return sqlConnection.preparedQuery(preparedQuery.sql())
      .execute(preparedQuery.tuple())
      .onComplete(logQuery(log, options.maxQueryTookTime(), startTime, preparedQuery.sql()))
      .map(RowUtil::firstOrNull);
  }

  private <T> Future<T> executeAndUpdatePreparedQuery(T entity, DAOQueryType queryType, boolean updateCurrentEntity) {
    final PreparedQuery preparedQuery = daoManager.getPreparedQueryEntity(entity, queryType, updateCurrentEntity);
    final long startTime = System.currentTimeMillis();
    return sqlConnection.preparedQuery(preparedQuery.sql())
      .execute(preparedQuery.tuple())
      .onComplete(logQuery(log, options.maxQueryTookTime(), startTime, preparedQuery.sql()))
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
    final long startTime = System.currentTimeMillis();
    return sqlConnection.preparedQuery(preparedQuery.sql())
      .executeBatch(tuples)
      .onComplete(logQuery(log, options.maxQueryTookTime(), startTime, preparedQuery.sql()))
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
    return preparedQuery(preparedQuery.sql())
      .execute(tuple)
      .map(RowUtil::firstOrNull);
  }

  @Override
  public Transaction getTransaction() {
    return transaction;
  }

  @Override
  public Future<Void> commitTransaction() {
    if (commited) {
      return Future.succeededFuture();
    }
    commited = true;
    return transaction.commit().onFailure(e -> commited = false);
  }

  @Override
  public Query<RowSet<Row>> query(String sql) {
    return sqlConnection.query(sql);
  }

  @Override
  public io.vertx.sqlclient.PreparedQuery<RowSet<Row>> preparedQuery(String sql) {
    return sqlConnection.preparedQuery(sql);
  }

  @Override
  public io.vertx.sqlclient.PreparedQuery<RowSet<Row>> preparedQuery(String sql, PrepareOptions options) {
    return sqlConnection.preparedQuery(sql, options);
  }

  @Override
  public SqlConnection prepare(String sql, Handler<AsyncResult<PreparedStatement>> handler) {
    return sqlConnection.prepare(sql, handler);
  }

  @Override
  public Future<PreparedStatement> prepare(String sql) {
    return sqlConnection.prepare(sql);
  }

  @Override
  public SqlConnection prepare(String sql, PrepareOptions options, Handler<AsyncResult<PreparedStatement>> handler) {
    return sqlConnection.prepare(sql, options, handler);
  }

  @Override
  public Future<PreparedStatement> prepare(String sql, PrepareOptions options) {
    return sqlConnection.prepare(sql, options);
  }

  @Override
  public SqlConnection exceptionHandler(Handler<Throwable> handler) {
    return sqlConnection.exceptionHandler(handler);
  }

  @Override
  public SqlConnection closeHandler(Handler<Void> handler) {
    return sqlConnection.closeHandler(handler);
  }

  @Override
  public void begin(Handler<AsyncResult<Transaction>> handler) {
    sqlConnection.begin(handler);
  }

  @Override
  public Future<Transaction> begin() {
    return sqlConnection.begin();
  }

  @Override
  public Transaction transaction() {
    return sqlConnection.transaction();
  }

  @Override
  public boolean isSSL() {
    return sqlConnection.isSSL();
  }

  @Override
  public void close(Handler<AsyncResult<Void>> handler) {
    sqlConnection.close(handler);
  }

  @Override
  public DatabaseMetadata databaseMetadata() {
    return sqlConnection.databaseMetadata();
  }

  @Override
  public Future<Void> close() {
    return sqlConnection.close();
  }

}
