package io.aitech.vertx.http;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.aitech.vertx.http.annotation.RequestBody;
import io.aitech.vertx.http.annotation.RequestHeaders;
import io.aitech.vertx.http.annotation.http.GET;
import io.aitech.vertx.http.annotation.http.POST;
import io.aitech.vertx.http.annotation.middleware.AuthMiddleware;
import io.aitech.vertx.http.annotation.middleware.Middleware;
import io.aitech.vertx.http.annotation.route.*;
import io.aitech.vertx.http.impl.RouterBuilderImpl;
import io.aitech.vertx.http.param.ParameterProvider;
import io.aitech.vertx.http.util.RouterUtil;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.impl.RouteImpl;
import io.vertx.junit5.VertxExtension;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Parameter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class RouterBuilderTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private <T> T await(Future<T> future) {
    return future.toCompletionStage().toCompletableFuture().join();
  }

  @Test
  void registerApis(Vertx vertx) {
    val router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    new RouterBuilderImpl()
      .registerModules(List.of(new ParamProviderFactory()))
      .append(router, List.of(new HelloWorld(), new MidTest(), new MidUnauthTest(), new MidAfterAuthTest()));

    val routes = router.getRoutes().stream()
      .map(route -> (RouteImpl) route)
      .toList();

    val sb = RouterUtil.getRouteList(router.getRoutes());
    log.info("Routes:\n" + sb);

    assertEquals(7, routes.size());

    await(vertx.createHttpServer().requestHandler(router).listen(8080));

    val webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8080));

    val request = new ReqModel().name("Programmers");
    val resp = await(webClient.post("/hello-world/hello")
      .putHeader("Content-Type", "application/json")
      .sendBuffer(Json.encodeToBuffer(request)));
    log.info("Headers: " + resp.headers());
    log.info("Response: " + resp.bodyAsString());
    assertEquals(200, resp.statusCode());
  }

  public static class ParamProviderFactory implements ParameterProvider.Factory {

    @Override
    public boolean isSupported(Parameter parameter) {
      return parameter.getType() == io.vertx.ext.web.Route.class;
    }

    @Override
    public ParameterProvider create(Parameter parameter) throws Throwable {
      return RoutingContext::currentRoute;
    }

  }

  @Data
  @Accessors(fluent = true)
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
  public static class ReqModel {

    private String name;

  }

  @Data
  @Accessors(fluent = true)
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
  public static class ReqHeaders {

    private String contentType;

  }

  @Route("/hello-world")
  public static class HelloWorld {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Buffer hello(@RequestHeaders ReqHeaders headers, @RequestBody ReqModel reqModel, io.vertx.ext.web.Route currentRoute) {
      log.info("request header content type: " + headers.contentType());
      log.info("hello called - " + reqModel.name());
      log.info("current route: " + currentRoute.getPath());
      return Buffer.buffer("Hello " + reqModel.name());
    }

    @GET
    @Authenticated
    @Disabled
    public void world() {

    }

  }

  @Middleware(beforeAuth = true)
  public static class MidUnauthTest implements Handler<RoutingContext> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(RoutingContext event) {
      log.info("Middleware Unauth event called");
      event.next();
    }

  }

  @AuthMiddleware
  public static class MidTest implements Handler<RoutingContext> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(RoutingContext event) {
      log.info("Auth Middleware event called");
      event.next();
    }

  }

  @Middleware
  public static class MidAfterAuthTest implements Handler<RoutingContext> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(RoutingContext event) {
      log.info("Middleware After Auth event called");
      event.next();
    }

  }


}
