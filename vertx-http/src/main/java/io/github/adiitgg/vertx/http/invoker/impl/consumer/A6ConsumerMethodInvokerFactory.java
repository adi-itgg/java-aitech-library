package io.github.adiitgg.vertx.http.invoker.impl.consumer;

import io.github.adiitgg.vertx.http.function.A6Consumer;
import io.github.adiitgg.vertx.http.invoker.MethodInvoker;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.github.adiitgg.vertx.http.util.ReflectionUtil;
import lombok.val;

import java.lang.reflect.Method;

public class A6ConsumerMethodInvokerFactory implements MethodInvoker.Factory {

  @Override
  public boolean isSupported(Method method) {
    return method.getReturnType().equals(Void.TYPE) && method.getParameterCount() == 6;
  }

  @SuppressWarnings("unchecked")
  @Override
  public MethodInvoker create(Object context, Method method, ParameterProvider.Factory parameterProviderFactory) throws Throwable {
    val a6Consumer = ReflectionUtil.createLambdaFactory(A6Consumer.class, method).apply(context);
    val params = parameterProviderFactory.provideParameters(method);
    return (ctx, options) -> {
      a6Consumer.accept(
        params[0].provide(ctx), params[1].provide(ctx), params[2].provide(ctx),
        params[3].provide(ctx), params[4].provide(ctx), params[5].provide(ctx)
      );
      return null;
    };
  }

}
