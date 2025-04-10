package io.github.adiitgg.vertx.config.yml.parser.impl;

import io.github.adiitgg.vertx.config.yml.parser.TypeParser;
import io.vertx.core.json.JsonArray;

public class JsonArrayTypeParser implements TypeParser {

  @Override
  public boolean isSupported(String value) {
    return value.length() > 1 && value.charAt(0) == '[' && value.charAt(value.length() - 1) == ']';
  }

  @Override
  public Object parse(String value) {
    return new JsonArray(value.replaceAll("\\\\\"", "\""));
  }

}
