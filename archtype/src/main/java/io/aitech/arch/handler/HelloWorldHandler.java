package io.aitech.arch.handler;

import io.aitech.arch.integrator.impl.GoogleIntegratorImpl;
import io.aitech.arch.model.dto.HelloRequest;
import io.aitech.arch.model.dto.HelloResponse;
import io.aitech.arch.model.mapper.HelloMapper;
import io.aitech.arch.repository.HelloWorldRepository;
import io.avaje.inject.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static io.vertx.core.Future.await;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelloWorldHandler {

    private final HelloWorldRepository helloWorldRepository;
    private final GoogleIntegratorImpl googleIntegrator;
    private final HelloMapper helloMapper;

    public String hello(String name) {
        return "Hello, " + name;
    }

    public void world() {
        await(googleIntegrator.checkConnection());
        log.info("Hello World");
    }

    public String dbTime() {
        return await(helloWorldRepository.currentTime()).toString();
    }


    public HelloResponse helloPost(HelloRequest request) {
        return helloMapper.toResponse(request);
    }

}
