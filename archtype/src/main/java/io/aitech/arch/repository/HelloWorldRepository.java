package io.aitech.arch.repository;

import io.vertx.core.Future;

import java.time.OffsetDateTime;

public interface HelloWorldRepository {

  Future<OffsetDateTime> currentTime();

}
