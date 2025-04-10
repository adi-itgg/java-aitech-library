package io.github.adiitgg.vertx.http.invoker.impl.function;

import io.github.adiitgg.vertx.http.function.A3Function;
import io.github.adiitgg.vertx.http.invoker.MethodInvoker;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.github.adiitgg.vertx.http.util.ReflectionUtil;
import lombok.val;

import java.lang.reflect.Method;

public class A3FunctionMethodInvokerFactory implements MethodInvoker.Factory {

  @Override
  public boolean isSupported(Method method) {
    return !method.getReturnType().equals(Void.TYPE) && method.getParameterCount() == 3;
  }

  @SuppressWarnings("unchecked")
  @Override
  public MethodInvoker create(Object context, Method method, ParameterProvider.Factory parameterProviderFactory) throws Throwable {
    val a3Function = ReflectionUtil.createLambdaFactory(A3Function.class, method).apply(context);
    val params = parameterProviderFactory.provideParameters(method);
    return (ctx, options) -> a3Function.apply(params[0].provide(ctx), params[1].provide(ctx), params[2].provide(ctx));
  }

}
