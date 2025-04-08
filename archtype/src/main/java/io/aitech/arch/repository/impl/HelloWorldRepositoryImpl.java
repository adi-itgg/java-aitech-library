package io.aitech.arch.repository.impl;

import io.aitech.arch.repository.HelloWorldRepository;
import io.aitech.vertx.db.orm.PgRepository;
import io.aitech.vertx.db.orm.util.RowUtil;
import io.avaje.inject.Component;
import io.vertx.core.Future;
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

}
