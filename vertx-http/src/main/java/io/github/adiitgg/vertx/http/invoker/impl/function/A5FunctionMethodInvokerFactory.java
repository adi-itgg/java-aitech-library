package io.github.adiitgg.vertx.http.invoker.impl.function;

import io.github.adiitgg.vertx.http.function.A5Function;
import io.github.adiitgg.vertx.http.invoker.MethodInvoker;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.github.adiitgg.vertx.http.util.ReflectionUtil;
import lombok.val;

import java.lang.reflect.Method;

public class A5FunctionMethodInvokerFactory implements MethodInvoker.Factory {

  @Override
  public boolean isSupported(Method method) {
    return !method.getReturnType().equals(Void.TYPE) && method.getParameterCount() == 5;
  }

  @SuppressWarnings("unchecked")
  @Override
  public MethodInvoker create(Object context, Method method, ParameterProvider.Factory parameterProviderFactory) throws Throwable {
    val a5Function = ReflectionUtil.createLambdaFactory(A5Function.class, method).apply(context);
    val params = parameterProviderFactory.provideParameters(method);
    return (ctx, options) -> a5Function.apply(
      params[0].provide(ctx), params[1].provide(ctx), params[2].provide(ctx), params[3].provide(ctx), params[4].provide(ctx)
    );
  }

}
