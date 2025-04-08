package io.aitech.arch.integrator.impl;

import io.aitech.arch.integrator.GoogleIntegrator;
import io.aitech.arch.platform.util.TimeUtil;
import io.avaje.inject.Component;
import io.avaje.inject.External;
import io.avaje.inject.PostConstruct;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClosedException;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleIntegratorImpl implements GoogleIntegrator {

  private final @External Vertx vertx;
  private WebClient webClient;
  private WebClientOptions webClientOptions;

  @PostConstruct
  public void onCreated() {
    this.webClientOptions = new WebClientOptions()
      .setDefaultHost("www.gstatic.com")
      .setDefaultPort(443);
    this.webClient = WebClient.create(vertx, webClientOptions);
  }

  private <T> Future<T> request(RequestOptions options, Object requestBody, Class<T> clazz) {
    final OffsetDateTime startTime = OffsetDateTime.now();
    final Buffer requestBuffer = requestBody == null ? Buffer.buffer() : Json.encodeToBuffer(requestBody);
    return this.webClient.request(options.getMethod(), options)
      .sendBuffer(requestBuffer)
      .onComplete(t -> log.info("#REQUEST {} {} {} {};req={};resp={}", options.getMethod().name(), TimeUtil.measureDynamicTookTime(startTime), t.succeeded() ? t.result().statusCode() : 0, webClientOptions.getDefaultHost() + ":" + webClientOptions.getDefaultPort() + options.getURI(), requestBuffer, t.succeeded() ? t.result().body() : ""))
      .map(resp -> clazz == null ? null : resp.bodyAsJson(clazz));
  }

  @Override
  public Future<Void> checkConnection() {
    final RequestOptions options = new RequestOptions()
      .setURI("/generate_204")
      .setMethod(HttpMethod.GET);
    return request(options, null, null).recover(e -> {
      if (e instanceof HttpClosedException) {
        return Future.succeededFuture();
      }
      return Future.failedFuture(e);
    }).mapEmpty();
  }

}
