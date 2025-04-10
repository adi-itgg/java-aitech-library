package io.github.adiitgg.vertx.http.invoker.impl.consumer;

import io.github.adiitgg.vertx.http.function.A7Consumer;
import io.github.adiitgg.vertx.http.invoker.MethodInvoker;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.github.adiitgg.vertx.http.util.ReflectionUtil;
import lombok.val;

import java.lang.reflect.Method;

public class A7ConsumerMethodInvokerFactory implements MethodInvoker.Factory {

  @Override
  public boolean isSupported(Method method) {
    return method.getReturnType().equals(Void.TYPE) && method.getParameterCount() == 7;
  }

  @SuppressWarnings("unchecked")
  @Override
  public MethodInvoker create(Object context, Method method, ParameterProvider.Factory parameterProviderFactory) throws Throwable {
    val a7Consumer = ReflectionUtil.createLambdaFactory(A7Consumer.class, method).apply(context);
    val params = parameterProviderFactory.provideParameters(method);
    return (ctx, options) -> {
      a7Consumer.accept(
        params[0].provide(ctx), params[1].provide(ctx), params[2].provide(ctx),
        params[3].provide(ctx), params[4].provide(ctx), params[5].provide(ctx),
        params[6].provide(ctx)
      );
      return null;
    };
  }

}
