package io.github.adiitgg.arch.repository.impl;

import io.avaje.inject.Component;
import io.github.adiitgg.arch.platform.qualifier.ConnectionDbTest;
import io.github.adiitgg.arch.repository.RepositoryDbTest;
import io.github.adiitgg.vertx.db.orm.PgRepository;
import io.github.adiitgg.vertx.db.orm.util.RowUtil;
import io.vertx.core.Future;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class RepositoryDbTestImpl implements RepositoryDbTest {

  private final @ConnectionDbTest PgRepository pgRepositoryDbTest;

  @Override
  public Future<OffsetDateTime> currentTime() {
    return pgRepositoryDbTest.query("SELECT now()")
      .execute()
      .map(rows -> RowUtil.first(rows).getOffsetDateTime(0));
  }
}
