package io.github.adiitgg.arch.repository.impl;

import io.github.adiitgg.arch.repository.HelloWorldRepository;
import io.github.adiitgg.vertx.db.orm.PgRepository;
import io.github.adiitgg.vertx.db.orm.util.RowUtil;
import io.avaje.inject.Component;
import io.vertx.core.Future;
import io.vertx.pgclient.data.Inet;
import io.vertx.sqlclient.Row;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class HelloWorldRepositoryImpl implements HelloWorldRepository {

  private final PgRepository pgRepository;

  @Override
  public Future<OffsetDateTime> currentTime() {
    return pgRepository.query("SELECT now()")
      .execute()
      .map(rows -> RowUtil.first(rows).getOffsetDateTime(0));
  }


  @Override
  public Future<String> dbInfo() {
    return pgRepository
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
