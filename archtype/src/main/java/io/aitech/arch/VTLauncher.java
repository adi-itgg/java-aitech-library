package io.aitech.arch;

import io.aitech.arch.verticle.MainVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.ThreadingModel;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxBuilder;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.concurrent.TimeUnit;

@Slf4j
public class VTLauncher extends Launcher {

  private boolean isCustomVertxOptions;

  public static void main(String[] args) {
    final VTLauncher launcher = new VTLauncher();
    if (args.length == 0) {
      launcher.dispatch(new String[]{"run", MainVerticle.class.getName()});
      return;
    }
    launcher.dispatch(args);
  }

  @Override
  public void dispatch(Object main, String[] args) {
    super.dispatch(main, args);
  }

  @Override
  public VertxBuilder createVertxBuilder(JsonObject config) {
    val envVertxOptions = System.getenv("VERTX_OPTIONS");
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

}
