package io.github.adiitgg.vertx.http.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class StringUtil {

  public static String toCamelCase(String input, char delimiter) {
    StringBuilder result = new StringBuilder();
    boolean capitalizeNext = false;
    for (char c : input.toCharArray()) {
      if (c == delimiter) {
        capitalizeNext = true;
      } else {
        if (capitalizeNext) {
          result.append(Character.toUpperCase(c));
          capitalizeNext = false;
        } else {
          result.append(c);
        }
      }
    }
    return result.toString();
  }

}
