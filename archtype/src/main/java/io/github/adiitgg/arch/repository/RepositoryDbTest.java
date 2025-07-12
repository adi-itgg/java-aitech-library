package io.github.adiitgg.arch.repository;

import io.vertx.core.Future;
import io.vertx.pgclient.data.Inet;

import java.time.OffsetDateTime;

public interface RepositoryDbTest {

  Future<OffsetDateTime> currentTime();

  Future<String> dbInfo();
}
