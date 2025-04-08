package io.aitech.vertx.http.invoker.impl.consumer;

import io.aitech.vertx.http.function.A3Consumer;
import io.aitech.vertx.http.invoker.MethodInvoker;
import io.aitech.vertx.http.param.ParameterProvider;
import io.aitech.vertx.http.util.ReflectionUtil;
import lombok.val;

import java.lang.reflect.Method;

public class A3ConsumerMethodInvokerFactory implements MethodInvoker.Factory {

  @Override
  public boolean isSupported(Method method) {
    return method.getReturnType().equals(Void.TYPE) && method.getParameterCount() == 3;
  }

  @SuppressWarnings("unchecked")
  @Override
  public MethodInvoker create(Object context, Method method, ParameterProvider.Factory parameterProviderFactory) throws Throwable {
    val a3Consumer = ReflectionUtil.createLambdaFactory(A3Consumer.class, method).apply(context);
    val params = parameterProviderFactory.provideParameters(method);
    return (ctx, options) -> {
      a3Consumer.accept(params[0].provide(ctx), params[1].provide(ctx), params[2].provide(ctx));
      return null;
    };
  }

}
