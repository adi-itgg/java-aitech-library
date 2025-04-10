package io.github.adiitgg.vertx.http;

import io.github.adiitgg.vertx.http.impl.RouterBuilderImpl;
import io.github.adiitgg.vertx.http.invoker.MethodInvoker;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.github.adiitgg.vertx.http.processor.RouteAnnotationProcessor;
import io.github.adiitgg.vertx.http.request.RequestReader;
import io.github.adiitgg.vertx.http.request.RequestValidation;
import io.github.adiitgg.vertx.http.response.HttpResponseLogging;
import io.github.adiitgg.vertx.http.response.ResponseMapper;
import io.github.adiitgg.vertx.http.response.ResponseWriter;
import io.github.adiitgg.vertx.http.route.OnRegisteringRoute;
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
