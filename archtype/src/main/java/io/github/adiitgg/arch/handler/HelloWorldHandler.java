package io.github.adiitgg.arch.handler;

import io.github.adiitgg.arch.integrator.impl.GoogleIntegratorImpl;
import io.github.adiitgg.arch.model.dto.HelloRequest;
import io.github.adiitgg.arch.model.dto.HelloResponse;
import io.github.adiitgg.arch.model.mapper.HelloMapper;
import io.github.adiitgg.arch.repository.HelloWorldRepository;
import io.avaje.inject.Component;
import io.github.adiitgg.arch.repository.RepositoryDbTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static io.vertx.core.Future.await;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelloWorldHandler {

    private final HelloWorldRepository helloWorldRepository;
    private final RepositoryDbTest repositoryDbTest;
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

    public String dbTimeTest() {
      return await(repositoryDbTest.currentTime()).toString();
    }

    public String dbPrimaryInfo() {
      return "Using db primary with info: " + await(helloWorldRepository.dbInfo());
    }

    public String dbSecondaryInfo() {
      return "Using db secondary with info: " + await(repositoryDbTest.dbInfo());
    }


    public HelloResponse helloPost(HelloRequest request) {
        return helloMapper.toResponse(request);
    }

}
