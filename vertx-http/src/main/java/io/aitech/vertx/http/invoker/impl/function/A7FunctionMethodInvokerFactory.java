package io.aitech.vertx.http.invoker.impl.function;

import io.aitech.vertx.http.function.A7Function;
import io.aitech.vertx.http.invoker.MethodInvoker;
import io.aitech.vertx.http.param.ParameterProvider;
import io.aitech.vertx.http.util.ReflectionUtil;
import lombok.val;

import java.lang.reflect.Method;

public class A7FunctionMethodInvokerFactory implements MethodInvoker.Factory {

  @Override
  public boolean isSupported(Method method) {
    return !method.getReturnType().equals(Void.TYPE) && method.getParameterCount() == 7;
  }

  @SuppressWarnings("unchecked")
  @Override
  public MethodInvoker create(Object context, Method method, ParameterProvider.Factory parameterProviderFactory) throws Throwable {
    val a7Function = ReflectionUtil.createLambdaFactory(A7Function.class, method).apply(context);
    val params = parameterProviderFactory.provideParameters(method);
    return (ctx, options) -> a7Function.apply(
      params[0].provide(ctx), params[1].provide(ctx), params[2].provide(ctx), params[3].provide(ctx),
      params[4].provide(ctx), params[5].provide(ctx), params[6].provide(ctx)
    );
  }

}
