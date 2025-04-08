package io.aitech.vertx.config.yml.parser;

public interface TypeParser {

  boolean isSupported(String value);

  Object parse(String value);

}
