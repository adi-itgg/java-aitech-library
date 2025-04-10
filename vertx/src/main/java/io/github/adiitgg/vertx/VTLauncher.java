package io.github.adiitgg.vertx;

import io.vertx.core.*;
import io.vertx.core.impl.VertxBuilder;
import io.vertx.core.json.JsonObject;
import lombok.val;

import java.util.concurrent.TimeUnit;

public class VTLauncher extends Launcher {

  private static Vertx VERTX_INSTANCE;

  private boolean isCustomVertxOptions;

  public static void main(String[] args) {
    final VTLauncher launcher = new VTLauncher();
    launcher.dispatch(args);
  }

  public static Vertx getVertxInstance() {
    return VERTX_INSTANCE;
  }

  @Override
  public void dispatch(Object main, String[] args) {
    super.dispatch(main, args);
  }

  @Override
  public VertxBuilder createVertxBuilder(JsonObject config) {
    var envVertxOptions = System.getProperty("vertx.options");
    if (envVertxOptions == null || envVertxOptions.isBlank()) {
      envVertxOptions = System.getenv("VERTX_OPTIONS");
    }
    if (envVertxOptions != null && !envVertxOptions.isBlank()) {
      this.isCustomVertxOptions = true;
      return super.createVertxBuilder(new JsonObject(envVertxOptions));
    }
    return super.createVertxBuilder(config);
  }

  @Override
  public void beforeStartingVertx(VertxOptions options) {
    if (this.isCustomVertxOptions) {
      return;
    }
    options.setBlockedThreadCheckInterval(TimeUnit.SECONDS.toMillis(60L))
      .setWarningExceptionTime(TimeUnit.SECONDS.toNanos(60L));
  }


  @Override
  public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
    if (deploymentOptions.getThreadingModel() == ThreadingModel.EVENT_LOOP) {
      deploymentOptions.setThreadingModel(ThreadingModel.VIRTUAL_THREAD);
    }
  }

  @Override
  public void afterStartingVertx(Vertx vertx) {
    VERTX_INSTANCE = vertx;
  }

}
