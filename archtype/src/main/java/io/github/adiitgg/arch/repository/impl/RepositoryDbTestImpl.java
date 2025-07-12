package io.github.adiitgg.arch.repository.impl;

import io.avaje.inject.Component;
import io.github.adiitgg.arch.platform.qualifier.ConnectionDbTest;
import io.github.adiitgg.arch.repository.RepositoryDbTest;
import io.github.adiitgg.vertx.db.orm.PgRepository;
import io.github.adiitgg.vertx.db.orm.util.RowUtil;
import io.vertx.core.Future;
import io.vertx.pgclient.data.Inet;
import io.vertx.sqlclient.Row;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Component
public class RepositoryDbTestImpl implements RepositoryDbTest {

  private final PgRepository pgRepositoryDbTest;

  public RepositoryDbTestImpl(@ConnectionDbTest PgRepository pgRepositoryDbTest) {
    this.pgRepositoryDbTest = pgRepositoryDbTest;
  }

  @Override
  public Future<OffsetDateTime> currentTime() {
    return pgRepositoryDbTest.query("SELECT now()")
      .execute()
      .map(rows -> RowUtil.first(rows).getOffsetDateTime(0));
  }

  @Override
  public Future<String> dbInfo() {
    return pgRepositoryDbTest
      .query("SELECT inet_client_addr(), inet_client_port(), current_database()")
      .execute()
      .map(rows -> {
        Row row = RowUtil.first(rows);
        if (row == null) return null;

        Inet ip = (Inet) row.getValue("inet_client_addr");
        Integer port = row.getInteger("inet_client_port");
        String db = row.getString("current_database");

        return String.format("IP: %s, Port: %d, DB: %s", ip.getAddress().toString(), port, db);
      });
  }
}
