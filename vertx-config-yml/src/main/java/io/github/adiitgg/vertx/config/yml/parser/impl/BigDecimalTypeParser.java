package io.github.adiitgg.vertx.config.yml.parser.impl;

import io.github.adiitgg.vertx.config.yml.parser.TypeParser;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class BigDecimalTypeParser implements TypeParser {

  private final Pattern allowedPattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");

  @Override
  public boolean isSupported(String value) {
    return allowedPattern.matcher(value).matches();
  }

  @Override
  public Object parse(String value) {
    return new BigDecimal(value);
  }

}
