package io.github.adiitgg.vertx.config.yml.parser;

public interface TypeParser {

  boolean isSupported(String value);

  Object parse(String value);

}
