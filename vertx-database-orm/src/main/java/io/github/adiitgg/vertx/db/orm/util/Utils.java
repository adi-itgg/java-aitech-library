package io.github.adiitgg.vertx.db.orm.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@UtilityClass
public final class Utils {

  public static final MethodHandles.Lookup lookup = MethodHandles.lookup();
  private static final Map<Class<?>, Class<?>> typeMapping = new HashMap<>();

  // why static? because for java optimization can make more fast like direct call
  private static final MethodHandle INSTANT_NOW;
  private static final MethodHandle OFFSETDATETIME_NOW;
  private static final MethodHandle OFFSETTIME_NOW;
  private static final MethodHandle LOCALTIME_NOW;
  private static final MethodHandle LOCALDATE_NOW;
  private static final MethodHandle LOCALDATETIME_NOW;
  private static final MethodHandle YEAR_NOW;
  private static final MethodHandle YEARMONTH_NOW;
  private static final MethodHandle ZONEDDATETIME_NOW;

  static {
    try {
      INSTANT_NOW = lookup.findStatic(Instant.class, "now", MethodType.methodType(Instant.class));
      OFFSETDATETIME_NOW = lookup.findStatic(OffsetDateTime.class, "now", MethodType.methodType(OffsetDateTime.class));
      OFFSETTIME_NOW = lookup.findStatic(OffsetTime.class, "now", MethodType.methodType(OffsetTime.class));
      LOCALTIME_NOW = lookup.findStatic(LocalTime.class, "now", MethodType.methodType(LocalTime.class));
      LOCALDATE_NOW = lookup.findStatic(LocalDate.class, "now", MethodType.methodType(LocalDate.class));
      LOCALDATETIME_NOW = lookup.findStatic(LocalDateTime.class, "now", MethodType.methodType(LocalDateTime.class));
      YEAR_NOW = lookup.findStatic(Year.class, "now", MethodType.methodType(Year.class));
      YEARMONTH_NOW = lookup.findStatic(YearMonth.class, "now", MethodType.methodType(YearMonth.class));
      ZONEDDATETIME_NOW = lookup.findStatic(ZonedDateTime.class, "now", MethodType.methodType(ZonedDateTime.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    typeMapping.put(boolean.class, Boolean.class);
    typeMapping.put(byte.class, Byte.class);
    typeMapping.put(short.class, Short.class);
    typeMapping.put(char.class, Character.class);
    typeMapping.put(int.class, Integer.class);
    typeMapping.put(long.class, Long.class);
    typeMapping.put(float.class, Float.class);
    typeMapping.put(double.class, Double.class);
  }


  /**
   * Converts a snake_case string to camelCase.
   * <p>
   * This method takes a string in snake_case format (where words are separated by underscores) and converts it to camelCase format (where words are joined together and each word after the first is capitalized).
   * <p>
   * If the input string is null or empty, it is returned unchanged.
   *
   * @param input the snake_case string to convert
   * @return the input string converted to camelCase
   * @example <pre>
   * snakeToCamel("snake_case") // returns "SnakeCase"
   * snakeToCamel("camel_case_example") // returns "CamelCaseExample"
   * snakeToCamel("_leading_underscore") // returns "LeadingUnderscore"
   * snakeToCamel("mixed_case_with_numbers_123") // returns "MixedCaseWithNumbers123"
   * </pre>
   * @since 1.0
   */
  public static String snakeToCamel(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }

    final int length = input.length();
    final StringBuilder result = new StringBuilder(length);
    boolean toUpperCase = false;

    for (int i = 0; i < length; i++) {
      char c = input.charAt(i);
      if (c == '_') {
        toUpperCase = true;
      } else {
        if (toUpperCase) {
          result.append(Character.toUpperCase(c));
          toUpperCase = false;
        } else {
          result.append(c);
        }
      }
    }

    return result.toString();
  }

  public static String toSnakeCase(String input) {
    if (input == null || input.isEmpty()) {
      return "";
    }

    final StringBuilder snakeCase = new StringBuilder();
    final char[] chars = input.toCharArray();

    for (char c : chars) {
      if (Character.isUpperCase(c)) {
        snakeCase.append('_').append(Character.toLowerCase(c));
      } else {
        snakeCase.append(c);
      }
    }

    if (snakeCase.charAt(0) == '_') {
      snakeCase.deleteCharAt(0);
    }

    return snakeCase.toString();
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  public static <T, R> Function<T, R> createGetter(Field field) {
    if (!field.trySetAccessible()) {
      throw new IllegalAccessException("can't access modifier field");
    }
    MethodHandle getter = lookup.unreflectGetter(field);
    MethodType type = getter.type();
    if (type.hasPrimitives()) {
      type = type.changeReturnType(typeMapping.get(type.returnType()));
      getter = getter.asType(type);
    }
    final CallSite site = LambdaMetafactory.metafactory(lookup,
      "apply", MethodType.methodType(Function.class, MethodHandle.class),
      type.erase(), MethodHandles.exactInvoker(type), type);
    return (Function<T, R>) site.getTarget().invokeExact(getter);
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  public static <T, V> BiConsumer<T, V> createSetter(Field field) {
    if (!field.trySetAccessible()) {
      throw new IllegalAccessException("can't access modifier field");
    }

    MethodHandle setter = lookup.unreflectSetter(field);
    MethodType type = setter.type();

    if (field.getType().isPrimitive()) {
      type = type.wrap().changeReturnType(void.class);
    }

    final CallSite site = LambdaMetafactory.metafactory(
      lookup,
      "accept",
      MethodType.methodType(BiConsumer.class, MethodHandle.class),
      type.erase(),
      MethodHandles.exactInvoker(setter.type()),
      type
    );

    return (BiConsumer<T, V>) site.getTarget().invokeExact(setter);
  }


  public static MethodHandle findTimeStaticMHByType(Class<?> type) {
    final MethodHandle methodHandle;
    if (type.equals(Instant.class)) {
      methodHandle = INSTANT_NOW;
    } else if (type.equals(OffsetDateTime.class)) {
      methodHandle = OFFSETDATETIME_NOW;
    } else if (type.equals(OffsetTime.class)) {
      methodHandle = OFFSETTIME_NOW;
    } else if (type.equals(LocalTime.class)) {
      methodHandle = LOCALTIME_NOW;
    } else if (type.equals(LocalDate.class)) {
      methodHandle = LOCALDATE_NOW;
    } else if (type.equals(LocalDateTime.class)) {
      methodHandle = LOCALDATETIME_NOW;
    } else if (type.equals(Year.class)) {
      methodHandle = YEAR_NOW;
    } else if (type.equals(YearMonth.class)) {
      methodHandle = YEARMONTH_NOW;
    } else if (type.equals(ZonedDateTime.class)) {
      methodHandle = ZONEDDATETIME_NOW;
    } else {
      throw new IllegalArgumentException("Unsupported type: " + type);
    }
    return methodHandle;
  }

}
