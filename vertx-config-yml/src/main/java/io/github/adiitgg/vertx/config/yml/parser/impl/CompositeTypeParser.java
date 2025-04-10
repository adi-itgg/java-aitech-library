package io.github.adiitgg.vertx.config.yml.parser.impl;

import io.github.adiitgg.vertx.config.yml.parser.TypeParser;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CompositeTypeParser implements TypeParser {

  private final List<TypeParser> typeParsers;

  @Override
  public boolean isSupported(String value) {
    return value != null && !value.isEmpty();
  }

  @Override
  public Object parse(String value) {
    for (TypeParser typeParser : typeParsers) {
      if (typeParser.isSupported(value)) {
        return typeParser.parse(value);
      }
    }
    return value;
  }

}
