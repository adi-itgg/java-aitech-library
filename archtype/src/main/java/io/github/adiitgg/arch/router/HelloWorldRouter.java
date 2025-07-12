package io.github.adiitgg.arch.router;

import io.github.adiitgg.arch.handler.HelloWorldHandler;
import io.github.adiitgg.arch.model.dto.HelloRequest;
import io.github.adiitgg.arch.model.dto.HelloResponse;
import io.github.adiitgg.vertx.http.annotation.RequestBody;
import io.github.adiitgg.vertx.http.annotation.http.GET;
import io.github.adiitgg.vertx.http.annotation.http.POST;
import io.github.adiitgg.vertx.http.annotation.route.Consumes;
import io.github.adiitgg.vertx.http.annotation.route.Produces;
import io.github.adiitgg.vertx.http.annotation.route.Route;
import io.avaje.inject.Component;
import io.vertx.core.http.HttpServerRequest;
import lombok.RequiredArgsConstructor;

@Component
@Route("/hello-world")
@Produces("application/json")
@RequiredArgsConstructor
public class HelloWorldRouter {

    private final HelloWorldHandler helloWorldHandler;

    @GET
    public String hello(HttpServerRequest request) {
        return helloWorldHandler.hello(request.getParam("name"));
    }

    @GET
    public void world() {
        helloWorldHandler.world();
    }

    @GET
    public String dbTime() {
        return helloWorldHandler.dbTime();
    }

    @GET
    public String dbTimeTest() {
      return helloWorldHandler.dbTimeTest();
    }

    @GET
    public String dbPrimaryInfo() {
      return helloWorldHandler.dbPrimaryInfo();
    }

    @GET
    public String dbSecondaryInfo() {
      return helloWorldHandler.dbSecondaryInfo();
    }

    @POST
    @Consumes("application/json")
    public HelloResponse helloPost(@RequestBody HelloRequest request) {
        return helloWorldHandler.helloPost(request);
    }

}
