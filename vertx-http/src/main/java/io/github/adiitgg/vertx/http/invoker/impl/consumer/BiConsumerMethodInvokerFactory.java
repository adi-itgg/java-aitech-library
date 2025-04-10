package io.github.adiitgg.vertx.http.invoker.impl.consumer;

import io.github.adiitgg.vertx.http.invoker.MethodInvoker;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.github.adiitgg.vertx.http.util.ReflectionUtil;
import lombok.val;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public class BiConsumerMethodInvokerFactory implements MethodInvoker.Factory {

  @Override
  public boolean isSupported(Method method) {
    return method.getReturnType().equals(Void.TYPE) && method.getParameterCount() == 2;
  }

  @SuppressWarnings("unchecked")
  @Override
  public MethodInvoker create(Object context, Method method, ParameterProvider.Factory parameterProviderFactory) throws Throwable {
    val biConsumer = ReflectionUtil.createLambdaFactory(BiConsumer.class, method).apply(context);
    val params = parameterProviderFactory.provideParameters(method);
    return (ctx, options) -> {
      biConsumer.accept(params[0].provide(ctx), params[1].provide(ctx));
      return null;
    };
  }

}
