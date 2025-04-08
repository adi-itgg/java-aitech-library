package io.aitech.vertx.http.invoker.impl;

import io.aitech.vertx.http.invoker.MethodInvoker;
import io.aitech.vertx.http.param.ParameterProvider;
import io.aitech.vertx.http.util.ReflectionUtil;
import lombok.val;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public class SupplierMethodInvokerFactory implements MethodInvoker.Factory {

  @Override
  public boolean isSupported(Method method) {
    return !method.getReturnType().equals(Void.TYPE) && method.getParameterCount() == 0;
  }

  @Override
  public MethodInvoker create(Object context, Method method, ParameterProvider.Factory parameterProviderFactory) throws Throwable {
    val supplier = ReflectionUtil.createLambdaFactory(Supplier.class, method).apply(context);
    return (ctx, options) -> supplier.get();
  }

}
