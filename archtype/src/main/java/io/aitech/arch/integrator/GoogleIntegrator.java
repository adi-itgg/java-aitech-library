package io.aitech.arch.integrator;

import io.vertx.core.Future;

public interface GoogleIntegrator {

  Future<Void> checkConnection();

}
