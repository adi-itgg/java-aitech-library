package io.aitech.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.ThreadingModel;
import io.vertx.core.impl.VertxImpl;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
public class VTLauncherTest {

  @Test
  void deployWithRunCommand(VertxTestContext testContext) {
    new VTLauncher().dispatch(new String[]{"run", MainVerticleTest.class.getName()});
    final VertxImpl vertxImpl = (VertxImpl) VTLauncher.getVertxInstance();
    vertxImpl.timer(100, TimeUnit.MILLISECONDS).onComplete(t -> {
      final String deploymentId = vertxImpl.deploymentIDs().stream().findFirst().orElse(null);
      MainVerticleTest verticleTest = (MainVerticleTest) vertxImpl.getDeployment(deploymentId)
        .getVerticles().stream().findFirst().orElse(null);
      if (verticleTest == null) {
        testContext.failNow("Deployment failed");
        return;
      }
      if (!verticleTest.isVirtualThread()) {
        testContext.failNow("Deployment failed not in virtual thread");
        return;
      }
      testContext.completeNow();
    });
  }

  public static final class MainVerticleTest extends AbstractVerticle {

    private boolean isVirtualThread;

    @Override
    public void start() {
      this.isVirtualThread = context.threadingModel() == ThreadingModel.VIRTUAL_THREAD;
    }

    public boolean isVirtualThread() {
      return isVirtualThread;
    }

  }

}
