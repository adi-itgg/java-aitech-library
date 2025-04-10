package io.github.adiitgg.vertx.http.invoker.impl.consumer;

import io.github.adiitgg.vertx.http.function.A5Consumer;
import io.github.adiitgg.vertx.http.invoker.MethodInvoker;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.github.adiitgg.vertx.http.util.ReflectionUtil;
import lombok.val;

import java.lang.reflect.Method;

public class A5ConsumerMethodInvokerFactory implements MethodInvoker.Factory {

  @Override
  public boolean isSupported(Method method) {
    return method.getReturnType().equals(Void.TYPE) && method.getParameterCount() == 5;
  }

  @SuppressWarnings("unchecked")
  @Override
  public MethodInvoker create(Object context, Method method, ParameterProvider.Factory parameterProviderFactory) throws Throwable {
    val a5Consumer = ReflectionUtil.createLambdaFactory(A5Consumer.class, method).apply(context);
    val params = parameterProviderFactory.provideParameters(method);
    return (ctx, options) -> {
      a5Consumer.accept(
        params[0].provide(ctx), params[1].provide(ctx), params[2].provide(ctx),
        params[3].provide(ctx), params[4].provide(ctx)
      );
      return null;
    };
  }

}
