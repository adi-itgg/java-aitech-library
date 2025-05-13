package io.github.adiitgg.vertx.http;

import io.github.adiitgg.vertx.http.annotation.QueryParams;
import io.github.adiitgg.vertx.http.annotation.RequestBody;
import io.github.adiitgg.vertx.http.annotation.http.GET;
import io.github.adiitgg.vertx.http.annotation.http.POST;
import io.github.adiitgg.vertx.http.annotation.route.Logs;
import io.github.adiitgg.vertx.http.annotation.route.Route;
import io.github.adiitgg.vertx.http.util.RouterUtil;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(VertxExtension.class)
public class RequestReaderTest {

  @Test
  public void queryParams(Vertx vertx) {
    val router = Router.router(vertx);

    val instances = List.<Object>of(new HiRouter());

    RouterBuilder.create(instances)
      .init(router)
      .withBodyHandler()
      .appendRouter();


    val sb = RouterUtil.getRouteList(router.getRoutes());
    log.info("Routes:\n" + sb);

    int port = new Random().nextInt(1000, 9999);

    await(vertx.createHttpServer().requestHandler(router).listen(port));

    val webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(port));

    val resp = await(webClient.get("/hi/query-params")
      .setQueryParam("name", "Programmers")
      .setQueryParam("age", "20")
      .setQueryParam("bornDate", "2000-01-01")
      .send());
    log.info("Headers: {}", resp.headers());
    log.info("Response: {}", resp.bodyAsString());

    assertEquals(200, resp.statusCode());
  }

  @Test
  public void bodyTest(Vertx vertx) {
    val router = Router.router(vertx);

    val instances = List.<Object>of(new HiRouter());

    RouterBuilder.create(instances)
      .init(router)
      .withBodyHandler()
      .appendRouter();


    val sb = RouterUtil.getRouteList(router.getRoutes());
    log.info("Routes:\n" + sb);

    int port = new Random().nextInt(1000, 9999);

    await(vertx.createHttpServer().requestHandler(router).listen(port));

    val webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(port));

    val resp = await(webClient.post("/hi/body")
      .sendJsonObject(JsonObject.of("name", "Programmers", "age", 20, "bornDate", "2000-01-01")));
    log.info("Headers: {}", resp.headers());
    log.info("Response: {}", resp.bodyAsString());

    assertEquals(200, resp.statusCode());
  }

  private <T> T await(Future<T> future) {
    return future.toCompletionStage().toCompletableFuture().join();
  }

  @Slf4j
  @Route("/hi")
  public static class HiRouter {

    @Data
    public static class QueryParamsTest {

      private String name;
      private Integer age;
      private LocalDate bornDate;

    }

    @GET("/query-params")
    public void hi(RoutingContext ctx, @QueryParams QueryParamsTest queryParamsTest) {
      if (queryParamsTest.name == null || queryParamsTest.name.isBlank()) {
        throw new IllegalArgumentException("name is required");
      }
      if (queryParamsTest.age == null) {
        throw new IllegalArgumentException("age is required");
      }
      if (queryParamsTest.bornDate == null) {
        throw new IllegalArgumentException("bornDate is required");
      }

      log.info("name: {}", queryParamsTest.name);
      log.info("age: {}", queryParamsTest.age);
      log.info("bornDate: {}", queryParamsTest.bornDate);
    }

    @Logs(request = false, response = false)
    @POST("/body")
    public JsonObject body(RoutingContext ctx, @RequestBody QueryParamsTest queryParamsTest) {
      log.info("body: {}", queryParamsTest);
      return JsonObject.of("success", true);
    }

  }

}
