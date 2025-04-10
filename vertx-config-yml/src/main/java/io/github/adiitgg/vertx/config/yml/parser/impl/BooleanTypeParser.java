package io.github.adiitgg.vertx.config.yml.parser.impl;

import io.github.adiitgg.vertx.config.yml.parser.TypeParser;

import java.util.Arrays;
import java.util.Locale;

public class BooleanTypeParser implements TypeParser {

  @Override
  public boolean isSupported(String value) {
    final String valueLowerCase = value.toLowerCase(Locale.ROOT);
    for (String s : Arrays.asList("true", "false", "yes", "no")) {
      if (valueLowerCase.equals(s)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Object parse(String value) {
    return Boolean.parseBoolean(value);
  }

}
