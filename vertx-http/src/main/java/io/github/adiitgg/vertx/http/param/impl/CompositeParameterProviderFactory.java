package io.github.adiitgg.vertx.http.param.impl;

import io.github.adiitgg.vertx.http.param.ParameterProvider;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

@RequiredArgsConstructor
public class CompositeParameterProviderFactory implements ParameterProvider.Factory {

  private final ParameterProvider.Factory[] factories;

  @Override
  public boolean isSupported(Parameter parameter) {
    for (ParameterProvider.Factory factory : factories) {
      if (factory.isSupported(parameter)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ParameterProvider create(Parameter parameter) throws Throwable {
    for (ParameterProvider.Factory factory : factories) {
      if (factory.isSupported(parameter)) {
        return factory.create(parameter);
      }
    }
    return null;
  }

  @Override
  public ParameterProvider[] provideParameters(Method method) {
    return Arrays.stream(method.getParameters()).map(parameter -> {
      val factory = Arrays.stream(factories).filter(f -> f.isSupported(parameter)).findFirst().orElse(null);
      if (factory == null) {
        throw new IllegalArgumentException("Unsupported parameter type: " + parameter.getType().getSimpleName() + ". No ParameterProvider.Factory found");
      }
      try {
        return factory.create(parameter);
      } catch (Throwable e) {
        throw new IllegalArgumentException("Unsupported parameter type: " + parameter.getType().getSimpleName(), e);
      }
    }).toArray(ParameterProvider[]::new);
  }

}
