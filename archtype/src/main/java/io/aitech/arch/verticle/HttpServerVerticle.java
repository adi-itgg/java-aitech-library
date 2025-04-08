package io.aitech.arch.verticle;

import io.aitech.arch.platform.handler.ExceptionHandler;
import io.aitech.arch.platform.model.BaseResponse;
import io.aitech.vertx.config.yml.YmlJsonObject;
import io.aitech.vertx.http.RouterBuilder;
import io.aitech.vertx.http.response.ResponseMapper;
import io.aitech.vertx.http.util.HttpUtil;
import io.aitech.vertx.http.util.RouterUtil;
import io.avaje.inject.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.concurrent.atomic.AtomicLong;

import static io.vertx.core.Future.await;

@Slf4j
@AssistFactory(HttpServerVerticle.Factory.class)
public class HttpServerVerticle extends AbstractVerticle {

  private final @External MainVerticle mainVerticle;
  private final ExceptionHandler exceptionHandler;
  private final BeanScope beanScope;
  private final ResponseMapper responseMapper;

  private final AtomicLong requestInProcess = new AtomicLong(0L);
  private final Promise<Void> shutdownPromise = Promise.promise();
  private boolean isShutdown;

  public HttpServerVerticle(@Assisted MainVerticle mainVerticle, ExceptionHandler exceptionHandler, ResponseMapper responseMapper) {
    this.mainVerticle = mainVerticle;
    this.exceptionHandler = exceptionHandler;
    this.beanScope = mainVerticle.beanScope();
    this.responseMapper = responseMapper;
  }

  public interface Factory {

    HttpServerVerticle create(MainVerticle mainVerticle);

  }

  @Override
  public void start() {
    val config = YmlJsonObject.of(config());
    val router = Router.router(vertx);
    val instances = beanScope.all().stream().map(BeanEntry::bean).toList();
    val routeBuilder = RouterBuilder.create(instances)
      .exceptionHandler(exceptionHandler)
      .responseMapper(responseMapper);

    router.route().handler(context -> {
      if (this.isShutdown) {
        context.request().connection().close();
        return;
      }

      mainVerticle.processedRequests().getAndIncrement();
      requestInProcess.getAndIncrement();

      context.addHeadersEndHandler(v -> {
        requestInProcess.decrementAndGet();
        if (this.isShutdown) {
          shutdownPromise.tryComplete();
        }
      });

      context.next();
    });

    routeBuilder.init(router)
      .withBodyHandler()
      .appendRouter();

    // not found
    router.route().handler(context -> {
      Buffer response = new BaseResponse()
        .code(HttpResponseStatus.NOT_FOUND.codeAsText().toString())
        .message("Not Found - What are you looking for? " + context.request().path())
        .toBuffer();
      context.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
      HttpUtil.end(context, response);
    });

    val listRoutes = RouterUtil.getRouteList(router.getRoutes());
    log.info(listRoutes);

    int port = config.getInteger("server.port", 8080);
    await(vertx.createHttpServer().requestHandler(router).listen(port));
    log.info("HTTP server started on port {}", port);
  }

  @Override
  public void stop() {
    this.isShutdown = true;
    if (requestInProcess.get() <= 0) {
      return;
    }
    log.warn("Please wait... Waiting for {} requests to finish", requestInProcess.get());
    // await all request to finish
    await(shutdownPromise.future());
    log.info("Finished all request");
  }

}
