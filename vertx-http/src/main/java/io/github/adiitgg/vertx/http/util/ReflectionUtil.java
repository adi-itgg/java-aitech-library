package io.github.adiitgg.vertx.http.util;

import io.github.adiitgg.vertx.http.function.UFunction;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;

@UtilityClass
public final class ReflectionUtil {

  private final MethodHandles.Lookup lookup = MethodHandles.lookup();

  /**
   * Create a lambda factory
   *
   * @param lambdaType can be Runnable, Function, Supplier, Consumer, Predicate or other interface
   * @param implMethod the target method
   * @return the lambda factory
   */
  @SneakyThrows
  public static <T, L> UFunction<T, L> createLambdaFactory(Class<? super L> lambdaType, Method implMethod) {
    Method lambdaMethod = findLambdaMethod(lambdaType);
    MethodType lambdaMethodType = MethodType.methodType(lambdaMethod.getReturnType(), lambdaMethod.getParameterTypes());

    Class<?> implType = implMethod.getDeclaringClass();

    MethodType implMethodType = MethodType.methodType(implMethod.getReturnType(), implMethod.getParameterTypes());
    MethodHandle implMethodHandle = lookup.findVirtual(implType, implMethod.getName(), implMethodType);

    MethodType invokedMethodType = MethodType.methodType(lambdaType, implType);

    CallSite metafactory = LambdaMetafactory.metafactory(
      lookup,
      lambdaMethod.getName(), invokedMethodType, lambdaMethodType,
      implMethodHandle, implMethodType);

    MethodHandle factory = metafactory.getTarget();
    return instance -> {
      @SuppressWarnings("unchecked")
      L lambda = (L) factory.invoke(instance);
      return lambda;
    };
  }


  public static Method findLambdaMethod(Class<?> type) {
    if (!type.isInterface()) {
      throw new IllegalArgumentException("This must be interface: " + type);
    }

    Method[] methods = getAllMethods(type);
    if (methods.length == 0) {
      throw new IllegalArgumentException("No methods in: " + type.getName());
    }

    Method targetMethod = null;
    for (Method method : methods) {
      if (!isInterfaceMethod(method)) {
        continue;
      }
      if (targetMethod != null) {
        throw new IllegalArgumentException("This isn't functional interface: " + type.getName());
      }
      targetMethod = method;
    }

    if (targetMethod == null) {
      throw new IllegalArgumentException("No method in: " + type.getName());
    }

    return targetMethod;
  }

  public static boolean isInterfaceMethod(Method method) {
    return !method.isDefault() && Modifier.isAbstract(method.getModifiers());
  }

  public static Method[] getAllMethods(Class<?> type) {
    LinkedList<Method> result = new LinkedList<>();
    Class<?> current = type;
    do {
      result.addAll(0, Arrays.asList(current.getDeclaredMethods()));
    } while ((current = current.getSuperclass()) != null);
    return result.toArray(new Method[0]);
  }

  @SuppressWarnings("unchecked")
  public <T extends Annotation> T getAnnotation(int modifiers, Annotation[] annotations, Class<T> annotation) {
    // skip static methods
    if (Modifier.isStatic(modifiers)) {
      return null;
    }

    // skip non public methods
    if (!Modifier.isPublic(modifiers)) {
      return null;
    }

    // verify if the method is annotated
    for (Annotation ann : annotations) {
      if (ann.annotationType().equals(annotation)) {
        return (T) ann;
      }
    }

    return null;
  }

  public <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotation) {
    return getAnnotation(clazz.getModifiers(), clazz.getAnnotations(), annotation);
  }

  public <T extends Annotation> T getAnnotation(Method method, Class<T> annotation) {
    return getAnnotation(method.getModifiers(), method.getAnnotations(), annotation);
  }

}
