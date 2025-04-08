package io.aitech.arch.router;

import io.aitech.arch.handler.HelloWorldHandler;
import io.aitech.arch.model.dto.HelloRequest;
import io.aitech.arch.model.dto.HelloResponse;
import io.aitech.vertx.http.annotation.RequestBody;
import io.aitech.vertx.http.annotation.http.GET;
import io.aitech.vertx.http.annotation.http.POST;
import io.aitech.vertx.http.annotation.route.Consumes;
import io.aitech.vertx.http.annotation.route.Produces;
import io.aitech.vertx.http.annotation.route.Route;
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

    @POST
    @Consumes("application/json")
    public HelloResponse helloPost(@RequestBody HelloRequest request) {
        return helloWorldHandler.helloPost(request);
    }

}
