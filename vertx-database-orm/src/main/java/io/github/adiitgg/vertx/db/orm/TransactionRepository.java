package io.github.adiitgg.vertx.db.orm;

import io.vertx.core.Future;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;

public interface TransactionRepository extends Repository, SqlConnection {

  Transaction getTransaction();

  Future<Void> commitTransaction();

}
