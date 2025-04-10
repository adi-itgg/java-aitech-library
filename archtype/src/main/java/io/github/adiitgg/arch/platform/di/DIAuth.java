package io.github.adiitgg.arch.platform.di;

import io.github.adiitgg.arch.platform.misc.EncryptionManager;
import io.github.adiitgg.vertx.config.yml.YmlJsonObject;
import io.avaje.inject.Bean;
import io.avaje.inject.External;
import io.avaje.inject.Factory;
import io.avaje.inject.RequiresBean;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import lombok.val;

@Factory
public final class DIAuth {


  @Bean
  JWTAuthOptions provideJWTAuthOptions(@External YmlJsonObject config, EncryptionManager encryptionManager) {
    val privateKeyString = config.getString("server.private-key").replace("\\n", "\n");
    val rsaPrivateKey = encryptionManager.getRSAPrivateKey(privateKeyString);
    val rsaPublicKey = encryptionManager.getRSAPublicKey(rsaPrivateKey);

    val jwtAuthOptions = new JWTAuthOptions(config.getJsonObject("server.jwt.auth-options"));
    jwtAuthOptions.addPubSecKey(
      new PubSecKeyOptions()
        .setAlgorithm(jwtAuthOptions.getJWTOptions().getAlgorithm())
        .setBuffer(privateKeyString)
    ).addPubSecKey(
      new PubSecKeyOptions()
        .setAlgorithm(jwtAuthOptions.getJWTOptions().getAlgorithm())
        .setBuffer(encryptionManager.convertPublicKeyToPEM(rsaPublicKey))
    );
    return jwtAuthOptions;
  }

  @Bean
  @RequiresBean(JWTAuthOptions.class)
  JWTAuth provideJWTAuth(@External Vertx vertx, @External JWTAuthOptions jwtAuthOptions) {
    return JWTAuth.create(vertx, jwtAuthOptions);
  }

}
