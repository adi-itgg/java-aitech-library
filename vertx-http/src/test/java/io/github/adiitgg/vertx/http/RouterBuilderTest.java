package io.github.adiitgg.vertx.http;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.adiitgg.vertx.http.annotation.RequestBody;
import io.github.adiitgg.vertx.http.annotation.RequestHeaders;
import io.github.adiitgg.vertx.http.annotation.http.GET;
import io.github.adiitgg.vertx.http.annotation.http.POST;
import io.github.adiitgg.vertx.http.annotation.http.PUT;
import io.github.adiitgg.vertx.http.annotation.middleware.AuthMiddleware;
import io.github.adiitgg.vertx.http.annotation.middleware.Middleware;
import io.github.adiitgg.vertx.http.annotation.route.*;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.github.adiitgg.vertx.http.util.RouterUtil;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.VertxImpl;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.impl.RouteImpl;
import io.vertx.junit5.VertxExtension;
import jakarta.validation.constraints.NotBlank;
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

    val instances = List.of(new ParamProviderFactory(), new HelloWorld(), new MidTest(), new MidUnauthTest(), new MidAfterAuthTest());

    RouterBuilder.create(instances)
      .init(router)
      .withBodyHandler()
      .appendRouter();


    val routes = router.getRoutes().stream()
      .map(route -> (RouteImpl) route)
      .toList();

    val sb = RouterUtil.getRouteList(router.getRoutes());
    log.info("Routes:\n" + sb);

    assertEquals(10, routes.size());

    await(vertx.createHttpServer().requestHandler(router).listen(8080));

    val webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8080));

    val request = new ReqModel().name("Programmers");
    val resp = await(webClient.post("/hello-world/hello")
      .putHeader("Content-Type", "application/json")
      .sendBuffer(Json.encodeToBuffer(request)));
    log.info("Headers: " + resp.headers());
    log.info("Response: " + resp.bodyAsString());

    val resperr = await(webClient.post("/hello-world/error")
      .putHeader("Content-Type", "application/json")
      .sendBuffer(Json.encodeToBuffer(request)));

    log.info("Error Headers: " + resperr.headers());
    log.info("Error Response: " + resperr.bodyAsString());

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

  @Data
  @Accessors(fluent = true)
  public static class ErrorRequest {

    @NotBlank
    private String error;

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

    @PUT
    @Authenticated
    public void world(RoutingContext ctx) {
      VertxImpl vertx = (VertxImpl) ctx.vertx();
      Context vtContext = vertx.createVirtualThreadContext();
      vtContext.runOnContext(v -> {
        log.info("Hello World");
        // TODO : do something (heavy task)
      });
    }

    @GET
    @Authenticated
    public void error(@RequestBody ErrorRequest errorRequest) {

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
