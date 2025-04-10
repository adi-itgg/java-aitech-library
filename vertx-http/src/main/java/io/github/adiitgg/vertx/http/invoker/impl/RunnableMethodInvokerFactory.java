package io.github.adiitgg.vertx.http.invoker.impl;

import io.github.adiitgg.vertx.http.invoker.MethodInvoker;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.github.adiitgg.vertx.http.util.ReflectionUtil;
import lombok.val;

import java.lang.reflect.Method;

public class RunnableMethodInvokerFactory implements MethodInvoker.Factory {

  @Override
  public boolean isSupported(Method method) {
    return method.getReturnType().equals(Void.TYPE) && method.getParameterCount() == 0;
  }

  @Override
  public MethodInvoker create(Object context, Method method, ParameterProvider.Factory parameterProviderFactory) throws Throwable {
    val runnable = ReflectionUtil.createLambdaFactory(Runnable.class, method).apply(context);
    return (ctx, options) -> {
      runnable.run();
      return null;
    };
  }

}
