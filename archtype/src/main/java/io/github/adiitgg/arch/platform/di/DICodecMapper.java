package io.github.adiitgg.arch.platform.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.vertx.core.json.Json;
import io.vertx.core.json.jackson.DatabindCodec;

@Factory
public final class DICodecMapper {

  @Bean
  ObjectMapper mapper() {
    return DatabindCodec.mapper();
  }

  @Bean
  DatabindCodec databindCodec() {
    return (DatabindCodec) Json.CODEC;
  }

}
