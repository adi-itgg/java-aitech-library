package io.github.adiitgg.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.impl.VertxImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(VertxExtension.class)
public class VTLauncherTest {

  @Test
  void deployWithRunCommand(VertxTestContext testContext) {
    new VTLauncher().dispatch(new String[]{"run", MainVerticleTest.class.getName()});
    checkDeployment(testContext);
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

  void checkDeployment(VertxTestContext testContext) {
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

  @Test
  void deployWithRunCommandCustomVertxOptions(VertxTestContext testContext) {
    System.setProperty("vertx.options", "{\"threadingModel\": \"EVENT_LOOP\"}");
    new VTLauncher().dispatch(new String[]{"run", MainVerticleTest.class.getName()});
    checkDeployment(testContext);
  }

  @Test
  void deployFromMainLauncher(VertxTestContext testContext) {
    VTLauncher.main(new String[]{"run", MainVerticleTest.class.getName()});
    checkDeployment(testContext);
  }

  @Test
  void shouldSetVirtualThreadModelForEventLoop() {
    DeploymentOptions options = new DeploymentOptions()
      .setThreadingModel(ThreadingModel.EVENT_LOOP);

    VTLauncher launcher = new VTLauncher();
    launcher.beforeDeployingVerticle(options);

    assertEquals(ThreadingModel.VIRTUAL_THREAD, options.getThreadingModel());
  }

  @Test
  void shouldNotChangeNonEventLoopThreadingModel() {
    DeploymentOptions options = new DeploymentOptions()
      .setThreadingModel(ThreadingModel.WORKER);

    VTLauncher launcher = new VTLauncher();
    launcher.beforeDeployingVerticle(options);

    assertEquals(ThreadingModel.WORKER, options.getThreadingModel());
  }

  @Test
  void shouldCreateVertxBuilderWithBlankPropertyOptions() {
    System.setProperty("vertx.options", "");

    VTLauncher launcher = new VTLauncher();
    var builder = launcher.createVertxBuilder(new JsonObject());

    assertNotNull(builder);
  }

}
