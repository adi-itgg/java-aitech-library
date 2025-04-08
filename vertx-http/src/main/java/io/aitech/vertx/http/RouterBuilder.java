package io.aitech.vertx.http;

import io.aitech.vertx.http.impl.RouterBuilderImpl;
import io.aitech.vertx.http.invoker.MethodInvoker;
import io.aitech.vertx.http.param.ParameterProvider;
import io.aitech.vertx.http.processor.RouteAnnotationProcessor;
import io.aitech.vertx.http.request.RequestReader;
import io.aitech.vertx.http.request.RequestValidation;
import io.aitech.vertx.http.response.HttpResponseLogging;
import io.aitech.vertx.http.response.ResponseMapper;
import io.aitech.vertx.http.response.ResponseWriter;
import io.aitech.vertx.http.route.OnRegisteringRoute;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.function.Function;

public interface RouterBuilder {

  static RouterBuilder create(List<Object> moduleAndRouters) {
    return new RouterBuilderImpl().registerModules(moduleAndRouters).routers(moduleAndRouters);
  }

  List<MethodInvoker.Factory> getMethodInvokerFactories();
  List<RequestReader> getRequestReaders();
  List<ResponseWriter> getResponseWriters();
  List<ParameterProvider.Factory> getParameterProviderFactories();
  List<RouteAnnotationProcessor> getRouteAnnotationProcessors();
  List<RequestValidation> getRequestValidations();
  List<OnRegisteringRoute> getOnRegisteringRoutes();


  RouterBuilder addMethodInvokerFactory(MethodInvoker.Factory methodInvokerFactory);

  RouterBuilder removeMethodInvokerFactory(Class<?> methodInvokerFactory);

  RouterBuilder addRequestReader(RequestReader requestReader);

  RouterBuilder removeRequestReader(Class<?> clazz);

  RouterBuilder addResponseWriter(ResponseWriter responseWriter);

  RouterBuilder removeResponseWriter(Class<?> clazz);

  RouterBuilder addParameterProviderFactory(ParameterProvider.Factory parameterProviderFactory);

  RouterBuilder removeParameterProviderFactory(Class<?> clazz);

  RouterBuilder addRouteAnnotationProcessor(RouteAnnotationProcessor routeAnnotationProcessor);

  RouterBuilder removeRouteAnnotationProcessor(Class<?> clazz);

  RouterBuilder addRequestValidation(RequestValidation requestValidation);

  RouterBuilder removeRequestValidation(Class<?> clazz);

  RouterBuilder setRequestValidations(List<RequestValidation> requestValidations);

  RouterBuilder registerModules(List<Object> classInstances);

  RouterBuilder httpResponseLogging(HttpResponseLogging httpResponseLogging);

  RouterBuilder addOnRegisteringRoute(OnRegisteringRoute listener);

  RouterBuilder removeOnRegisteringRoute(OnRegisteringRoute listener);

  RouterBuilder exceptionHandler(Function<RoutingContext, Buffer> exceptionHandler);

  RouterBuilder responseMapper(ResponseMapper responseMapper);

  RouterBuilder init(Router router, String defaultResponseContentType);

  RouterBuilder init(Router router);

  RouterBuilder withBodyHandler();

  RouterBuilder routers(List<Object> routerInstances);

  RouterBuilder appendRouter();

  RouterBuilder append(List<Object> classInstances);

  RouterBuilder append(Router router, List<Object> classInstances);

}
