package io.github.adiitgg.arch.repository;

import io.vertx.core.Future;

import java.time.OffsetDateTime;

public interface RepositoryDbTest {

  Future<OffsetDateTime> currentTime();
}
