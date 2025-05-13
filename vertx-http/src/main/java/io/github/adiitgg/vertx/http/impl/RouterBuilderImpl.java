package io.github.adiitgg.vertx.http.impl;

import io.github.adiitgg.vertx.http.RouterBuilder;
import io.github.adiitgg.vertx.http.annotation.http.API;
import io.github.adiitgg.vertx.http.annotation.middleware.AuthMiddleware;
import io.github.adiitgg.vertx.http.annotation.middleware.Middleware;
import io.github.adiitgg.vertx.http.annotation.route.Authenticated;
import io.github.adiitgg.vertx.http.annotation.route.Route;
import io.github.adiitgg.vertx.http.invoker.MethodInvoker;
import io.github.adiitgg.vertx.http.invoker.impl.RunnableMethodInvokerFactory;
import io.github.adiitgg.vertx.http.invoker.impl.SupplierMethodInvokerFactory;
import io.github.adiitgg.vertx.http.invoker.impl.consumer.*;
import io.github.adiitgg.vertx.http.invoker.impl.function.*;
import io.github.adiitgg.vertx.http.model.*;
import io.github.adiitgg.vertx.http.param.ParameterProvider;
import io.github.adiitgg.vertx.http.param.impl.*;
import io.github.adiitgg.vertx.http.processor.RouteAnnotationProcessor;
import io.github.adiitgg.vertx.http.processor.impl.*;
import io.github.adiitgg.vertx.http.request.RequestReader;
import io.github.adiitgg.vertx.http.request.RequestValidation;
import io.github.adiitgg.vertx.http.request.impl.*;
import io.github.adiitgg.vertx.http.request.impl.validation.JakartaValidationRequest;
import io.github.adiitgg.vertx.http.response.HttpResponseLogging;
import io.github.adiitgg.vertx.http.response.ResponseMapper;
import io.github.adiitgg.vertx.http.response.ResponseWriter;
import io.github.adiitgg.vertx.http.response.impl.CompositeResponseWriter;
import io.github.adiitgg.vertx.http.response.impl.GeneralResponseWriter;
import io.github.adiitgg.vertx.http.response.impl.HttpResponseLoggingImpl;
import io.github.adiitgg.vertx.http.response.impl.JsonResponseWriter;
import io.github.adiitgg.vertx.http.route.MiddlewareHandler;
import io.github.adiitgg.vertx.http.route.OnRegisteringRoute;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RouterBuilderImpl implements RouterBuilder {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final @Getter List<MethodInvoker.Factory> methodInvokerFactories = new ArrayList<>();
  private final @Getter List<RequestReader> requestReaders = new ArrayList<>();
  private final @Getter List<ResponseWriter> responseWriters = new ArrayList<>();
  private final @Getter List<ParameterProvider.Factory> parameterProviderFactories = new ArrayList<>();
  private final @Getter List<RouteAnnotationProcessor> routeAnnotationProcessors = new ArrayList<>();
  private final @Getter List<RequestValidation> requestValidations = new ArrayList<>();
  private final @Getter List<OnRegisteringRoute> onRegisteringRoutes = new ArrayList<>();
  private HttpResponseLogging httpResponseLogging;
  private Function<RoutingContext, Buffer> exceptionHandler;
  private ResponseMapper responseMapper;
  private Router router;
  private List<Object> routerInstances;

  public RouterBuilderImpl() {
    val databindCodec = (DatabindCodec) Json.CODEC;
    val objectMapper = DatabindCodec.mapper().findAndRegisterModules();

    // RequestReaders
    this.requestReaders.add(new JsonRequestBodyReader(databindCodec));
    this.requestReaders.add(new QueryParamsRequestReader(databindCodec));
    this.requestReaders.add(new HeadersRequestReader(databindCodec));
    this.requestReaders.add(new PathParamRequestReader(databindCodec));
    this.requestReaders.add(new FormRequestReader(databindCodec));

    // ResponseWriters
    this.responseWriters.add(new JsonResponseWriter());
    this.responseWriters.add(new GeneralResponseWriter());

    // ParameterProviders
    val compositeRequestReaders = new CompositeRequestReader(requestReaders, requestValidations);
    this.parameterProviderFactories.add(new RoutingContextParamProviderFactory());
    this.parameterProviderFactories.add(new HttpServerRequestParamProviderFactory());
    this.parameterProviderFactories.add(new HttpServerResponseParamProviderFactory());
    this.parameterProviderFactories.add(new RequestBodyParamProviderFactory(compositeRequestReaders));
    this.parameterProviderFactories.add(new RequestQueryParamProviderFactory(objectMapper));
    this.parameterProviderFactories.add(new RequestQueryParamsProviderFactory(compositeRequestReaders));
    this.parameterProviderFactories.add(new RequestHeadersParamProviderFactory(compositeRequestReaders));
    this.parameterProviderFactories.add(new RequestPathParamProviderFactory(compositeRequestReaders));
    this.parameterProviderFactories.add(new RequestFormParamProviderFactory(compositeRequestReaders));
    this.parameterProviderFactories.add(new UserParamProviderFactory());
    this.parameterProviderFactories.add(new UserContextParamProviderFactory());
    this.parameterProviderFactories.add(new VertxParamProviderFactory());
    this.parameterProviderFactories.add(new ContextDataParamProviderFactory());
    this.parameterProviderFactories.add(new SessionParamProviderFactory());

    // MethodInvokers
    this.methodInvokerFactories.add(new RunnableMethodInvokerFactory());
    this.methodInvokerFactories.add(new SupplierMethodInvokerFactory());
    this.methodInvokerFactories.add(new ConsumerMethodInvokerFactory());
    this.methodInvokerFactories.add(new BiConsumerMethodInvokerFactory());
    this.methodInvokerFactories.add(new A3ConsumerMethodInvokerFactory());
    this.methodInvokerFactories.add(new A4ConsumerMethodInvokerFactory());
    this.methodInvokerFactories.add(new A5ConsumerMethodInvokerFactory());
    this.methodInvokerFactories.add(new A6ConsumerMethodInvokerFactory());
    this.methodInvokerFactories.add(new A7ConsumerMethodInvokerFactory());
    this.methodInvokerFactories.add(new FunctionMethodInvokerFactory());
    this.methodInvokerFactories.add(new BiFunctionMethodInvokerFactory());
    this.methodInvokerFactories.add(new A3FunctionMethodInvokerFactory());
    this.methodInvokerFactories.add(new A4FunctionMethodInvokerFactory());
    this.methodInvokerFactories.add(new A5FunctionMethodInvokerFactory());
    this.methodInvokerFactories.add(new A6FunctionMethodInvokerFactory());
    this.methodInvokerFactories.add(new A7FunctionMethodInvokerFactory());

    // RouteAnnotationProcessors
    this.routeAnnotationProcessors.add(new AuthenticatedRouteAnnotationProcessor());
    this.routeAnnotationProcessors.add(new ConsumesRouteAnnotationProcessor());
    this.routeAnnotationProcessors.add(new DisabledRouteAnnotationProcessor());
    this.routeAnnotationProcessors.add(new NameRouteAnnotationProcessor());
    this.routeAnnotationProcessors.add(new ExcludeMethodPathRouteAnnotationProcessor());
    this.routeAnnotationProcessors.add(new NoResponseWriterRouteAnnotationProcessor());
    this.routeAnnotationProcessors.add(new OrderRouteAnnotationProcessor());
    this.routeAnnotationProcessors.add(new PathRegexRouteAnnotationProcessor());
    this.routeAnnotationProcessors.add(new ProducesRouteAnnotationProcessor());
    this.routeAnnotationProcessors.add(new VirtualHostRouteAnnotationProcessor());
    this.routeAnnotationProcessors.add(new BlockingRouteAnnotationProcessor());
    this.routeAnnotationProcessors.add(new LogsRouteAnnotationProcessor());

    // RequestValidations
    this.requestValidations.add(new JakartaValidationRequest());

    // Default
    this.httpResponseLogging = new HttpResponseLoggingImpl();
    this.exceptionHandler = new DefaultExceptionHandler();
  }

  protected <T> RouterBuilderImpl checkAddFirst(List<T> list, T t) {
    if (t == null) {
      return this;
    }
    for (T t1 : list) {
      if (t1.getClass().equals(t.getClass())) {
        return this;
      }
    }
    list.add(t);
    return this;
  }

  protected <T> RouterBuilderImpl checkRemove(List<T> list, Class<?> clazz) {
    list.removeIf(context -> context.getClass().equals(clazz));
    return this;
  }

  @Override
  public RouterBuilderImpl addMethodInvokerFactory(MethodInvoker.Factory methodInvokerFactory) {
    return checkAddFirst(methodInvokerFactories, methodInvokerFactory);
  }

  @Override
  public RouterBuilderImpl removeMethodInvokerFactory(Class<?> clazz) {
    return checkRemove(methodInvokerFactories, clazz);
  }

  @Override
  public RouterBuilderImpl addRequestReader(RequestReader requestReader) {
    return checkAddFirst(requestReaders, requestReader);
  }

  @Override
  public RouterBuilderImpl removeRequestReader(Class<?> clazz) {
    return checkRemove(requestReaders, clazz);
  }

  @Override
  public RouterBuilderImpl addResponseWriter(ResponseWriter responseWriter) {
    return checkAddFirst(responseWriters, responseWriter);
  }

  @Override
  public RouterBuilderImpl removeResponseWriter(Class<?> clazz) {
    return checkRemove(responseWriters, clazz);
  }

  @Override
  public RouterBuilderImpl addParameterProviderFactory(ParameterProvider.Factory parameterProviderFactory) {
    return checkAddFirst(parameterProviderFactories, parameterProviderFactory);
  }

  @Override
  public RouterBuilderImpl removeParameterProviderFactory(Class<?> clazz) {
    return checkRemove(parameterProviderFactories, clazz);
  }

  @Override
  public RouterBuilderImpl addRouteAnnotationProcessor(RouteAnnotationProcessor routeAnnotationProcessor) {
    return checkAddFirst(routeAnnotationProcessors, routeAnnotationProcessor);
  }

  @Override
  public RouterBuilderImpl removeRouteAnnotationProcessor(Class<?> clazz) {
    return checkRemove(routeAnnotationProcessors, clazz);
  }

  @Override
  public RouterBuilderImpl addRequestValidation(RequestValidation requestValidation) {
    return checkAddFirst(requestValidations, requestValidation);
  }

  @Override
  public RouterBuilderImpl removeRequestValidation(Class<?> clazz) {
    return checkRemove(requestReaders, clazz);
  }

  @Override
  public RouterBuilderImpl setRequestValidations(List<RequestValidation> requestValidations) {
    this.requestValidations.clear();
    this.requestValidations.addAll(requestValidations);
    return this;
  }

  protected Class<?> isAssignable(Class<?> from, Class<?> to, Class<?> target) {
    if (target != null) {
      return target;
    }
    if (to.isAssignableFrom(from)) {
      return to;
    }
    return null;
  }

  @Override
  public RouterBuilderImpl registerModules(List<Object> modules) {
    val grouped = modules.stream().collect(Collectors.groupingBy(module -> {
      val clazz = module.getClass();
      Class<?> target = null;
      target = isAssignable(clazz, MethodInvoker.Factory.class, target);
      target = isAssignable(clazz, RequestReader.class, target);
      target = isAssignable(clazz, ResponseWriter.class, target);
      target = isAssignable(clazz, ParameterProvider.Factory.class, target);
      target = isAssignable(clazz, RouteAnnotationProcessor.class, target);
      target = isAssignable(clazz, RequestValidation.class, target);
      return target == null ? clazz : target;
    }));

    // MethodInvokers
    grouped.getOrDefault(MethodInvoker.Factory.class, Collections.emptyList())
      .stream().map(context -> (MethodInvoker.Factory) context)
      .forEach(this::addMethodInvokerFactory);

    // RequestReaders
    grouped.getOrDefault(RequestReader.class, Collections.emptyList())
      .stream().map(context -> (RequestReader) context)
      .forEach(this::addRequestReader);

    // ResponseWriters
    grouped.getOrDefault(ResponseWriter.class, Collections.emptyList())
      .stream().map(context -> (ResponseWriter) context)
      .forEach(this::addResponseWriter);

    // ParameterProviders
    grouped.getOrDefault(ParameterProvider.Factory.class, Collections.emptyList())
      .stream().map(context -> (ParameterProvider.Factory) context)
      .forEach(this::addParameterProviderFactory);

    // RouteAnnotationProcessors
    grouped.getOrDefault(RouteAnnotationProcessor.class, Collections.emptyList())
      .stream().map(context -> (RouteAnnotationProcessor) context)
      .forEach(this::addRouteAnnotationProcessor);

    // RequestValidations
    grouped.getOrDefault(RequestValidation.class, Collections.emptyList())
      .stream().map(context -> (RequestValidation) context)
      .forEach(this::addRequestValidation);

    return this;
  }

  @Override
  public RouterBuilderImpl httpResponseLogging(HttpResponseLogging httpResponseLogging) {
    this.httpResponseLogging = httpResponseLogging;
    return this;
  }

  @Override
  public RouterBuilderImpl addOnRegisteringRoute(OnRegisteringRoute listener) {
    this.onRegisteringRoutes.add(listener);
    return this;
  }

  @Override
  public RouterBuilderImpl removeOnRegisteringRoute(OnRegisteringRoute listener) {
    this.onRegisteringRoutes.remove(listener);
    return this;
  }

  @Override
  public RouterBuilderImpl exceptionHandler(Function<RoutingContext, Buffer> exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
    return this;
  }

  @Override
  public RouterBuilderImpl responseMapper(ResponseMapper responseMapper) {
    this.responseMapper = responseMapper;
    return this;
  }

  @Override
  public RouterBuilderImpl init(Router router, String defaultResponseContentType) {
    this.router = router;
    router.route().handler(context -> {
      context.put(RoutingData.REQUEST_START_OFFSETDATETIME, OffsetDateTime.now());
      context.response().putHeader(HttpHeaders.CONTENT_TYPE, defaultResponseContentType);
      // for logging
      context.addEndHandler(v -> {
        Buffer buffer = context.get(RoutingData.RESPONSE_BODY_BUFFER);
        httpResponseLogging.log(context, buffer);
      });
      context.next();
    });
    return this;
  }

  @Override
  public RouterBuilderImpl init(Router router) {
    return init(router, HttpHeaderValues.APPLICATION_JSON.toString());
  }

  @Override
  public RouterBuilderImpl withBodyHandler() {
    this.router.route().handler(BodyHandler.create());
    return this;
  }

  @Override
  public RouterBuilderImpl routers(List<Object> routerInstances) {
    this.routerInstances = routerInstances;
    return this;
  }

  @Override
  public RouterBuilderImpl appendRouter() {
    return append(routerInstances);
  }

  @Override
  public RouterBuilderImpl append(List<Object> classInstances) {
    return append(this.router, classInstances);
  }


  @Override
  public RouterBuilderImpl append(Router router, List<Object> classInstances) {
    val compositeResponseWriter = new CompositeResponseWriter(responseWriters.toArray(new ResponseWriter[0]), responseMapper);
    val compositeParameterProviderFactory = new CompositeParameterProviderFactory(parameterProviderFactories.toArray(new ParameterProvider.Factory[0]));
    val compositeRouteAnnotationProcessor = new CompositeRouteAnnotationProcessor(routeAnnotationProcessors.toArray(new RouteAnnotationProcessor[0]));

    val middlewaresAuth = classInstances.stream()
      .map(context -> getMiddleware(context, compositeRouteAnnotationProcessor))
      .filter(Objects::nonNull)
      .collect(Collectors.groupingBy(context -> context.options().auth()));

    val groupedMiddlewares = middlewaresAuth.getOrDefault(false, Collections.emptyList()).stream()
      .collect(Collectors.groupingBy(context -> context.options().getOrDefault("beforeAuth", true)));

    val middlewaresBeforeAuth = new AtomicReference<>(groupedMiddlewares.getOrDefault(true, Collections.emptyList()));
    val middlewaresAfterAuth = new AtomicReference<>(groupedMiddlewares.getOrDefault(false, Collections.emptyList()));

    val authMiddlewares = new AtomicReference<>(middlewaresAuth.getOrDefault(true, Collections.emptyList()));

    val groupedApiMethods = classInstances.stream()
      .filter(context -> context != null && context.getClass().isAnnotationPresent(Route.class))
      .flatMap(context -> findApiMethods(context, compositeRouteAnnotationProcessor).stream().filter(Objects::nonNull))
      .collect(Collectors.groupingBy(ma -> ma.options().auth()));

    // unAuth middleware
    Runnable registerMiddleware = () -> middlewaresBeforeAuth.set(tryRegisterMiddleware(router, middlewaresBeforeAuth.get()));
    registerMiddleware.run();


    // unAuth api
    for (MethodApi methodApi : groupedApiMethods.getOrDefault(false, Collections.emptyList())) {
      registerRoute(router, methodApi, compositeParameterProviderFactory, compositeResponseWriter);
      registerMiddleware.run();
    }

    // Auth middleware
    final Runnable registerAuthMiddleware = () -> authMiddlewares.set(tryRegisterMiddleware(router, authMiddlewares.get()));
    registerAuthMiddleware.run();

    // unAuth middleware
    registerMiddleware = () -> middlewaresAfterAuth.set(tryRegisterMiddleware(router, middlewaresAfterAuth.get()));
    registerMiddleware.run();

    // Auth api
    for (MethodApi methodApi : groupedApiMethods.getOrDefault(true, Collections.emptyList())) {
      registerRoute(router, methodApi, compositeParameterProviderFactory, compositeResponseWriter);
      registerAuthMiddleware.run();
      registerMiddleware.run();
    }

    if (!middlewaresBeforeAuth.get().isEmpty()) {
      log.warn("No route registered for: " + middlewaresBeforeAuth.get());
    }

    if (!authMiddlewares.get().isEmpty()) {
      log.warn("No route registered for: " + authMiddlewares.get());
    }

    if (!middlewaresAfterAuth.get().isEmpty()) {
      log.warn("No route registered for: " + middlewaresAfterAuth.get());
    }

    // register exception handler
    if (this.exceptionHandler != null) {
      router.route().failureHandler(context -> compositeResponseWriter.write(context, this.exceptionHandler.apply(context)));
    }

    return this;
  }

  @SuppressWarnings("unchecked")
  protected List<RouteContext> tryRegisterMiddleware(Router router, List<RouteContext> list) {
    return list.stream().filter(context -> {
      val options = context.options();
      var route = router.route();

      if (options.method() != null) {
        route = route.method(HttpMethod.valueOf(options.method().name()));
      }

      if (options.consumes() != null) {
        for (String consume : options.consumes()) {
          route = route.consumes(consume);
        }
      }

      if (options.produces() != null) {
        for (String produce : options.produces()) {
          route = route.produces(produce);
        }
      }

      if (options.order() != null) {
        route = route.order(options.order());
      }

      if (!options.enable()) {
        route = route.disable();
      }

      if (options.virtualHost() != null) {
        route = route.virtualHost(options.virtualHost());
      }

      if (options.name() != null) {
        route = route.setName(options.name());
      }

      if (context.context() instanceof MiddlewareHandler mh) {
        route = mh.onRegister(route);
        if (route != null) {
          if (options.isBlocking()) {
            route.blockingHandler(mh);
          } else {
            route.handler(mh);
          }
          return false;
        }
      } else if (context.context() instanceof Handler<?> handler) {
        val h = (Handler<RoutingContext>) handler;
        if (options.isBlocking()) {
          route.blockingHandler(h);
        } else {
          route.handler(h);
        }
        return false;
      }
      return true;
    }).toList();
  }

  protected <T extends Annotation> boolean isMiddleware(Object context, Class<T> clazz) {
    return context.getClass().isAnnotationPresent(clazz) && context instanceof Handler<?>;
  }

  protected RouteContext getMiddleware(Object context, RouteAnnotationProcessor routeAnnotationProcessor) {
    val isMiddleware = isMiddleware(context, Middleware.class);
    val isAuthMiddleware = isMiddleware(context, AuthMiddleware.class);
    if (!isMiddleware && !isAuthMiddleware) {
      return null;
    }
    val qualifiers = Arrays.stream(context.getClass().getDeclaredAnnotations())
      .filter(annotation -> annotation.annotationType().isAnnotationPresent(Authenticated.class))
      .toList();

    final RouteOptions options = new RouteOptions();

    routeAnnotationProcessor.process(context, context.getClass(), null, options);

    options.auth(isAuthMiddleware);
    options.put("qualifiers", qualifiers); // TODO implement qualifiers for authentication

    if (isMiddleware) {
      val middleware = context.getClass().getAnnotation(Middleware.class);
      options.put("beforeAuth", middleware.beforeAuth());
    }

    return new RouteContext().context(context).options(options);
  }

  protected List<MethodApi> findApiMethods(Object context, RouteAnnotationProcessor routeAnnotationProcessor) {
    val route = context.getClass().getAnnotation(Route.class);

    return Arrays.stream(context.getClass().getMethods()).map(method -> {
      val httpMethodAnnotation = Arrays.stream(method.getDeclaredAnnotations())
        .filter(annotation -> annotation.annotationType().isAnnotationPresent(API.class))
        .findFirst();
      if (httpMethodAnnotation.isEmpty()) {
        return null;
      }

      val apiAnnotation = httpMethodAnnotation.get().annotationType().getAnnotation(API.class);
      val annotation = httpMethodAnnotation.get();
      val params = Arrays.stream(annotation.getClass().getDeclaredMethods())
        .filter(m -> !m.getReturnType().equals(Void.class) && m.getParameterCount() == 0)
        .collect(Collectors.toMap(Method::getName, m -> {
          try {
            return m.invoke(annotation);
          } catch (Throwable e) {
            log.error("Failed to invoke method: " + m.getName(), e);
            throw new IllegalArgumentException(e.getMessage());
          }
        }));

      val optionsJson = new JsonObject(params);
      optionsJson.put("method", apiAnnotation.method().name());
      optionsJson.put("path", optionsJson.getValue("value"));

      val options = optionsJson.mapTo(RouteOptions.class);
      routeAnnotationProcessor.process(context, context.getClass(), method, options);

      val factory = this.methodInvokerFactories.stream().filter(f -> f.isSupported(method)).findFirst().orElse(null);
      if (factory == null) {
        throw new IllegalArgumentException("Unsupported method: " + method.getName() + ". Missing MethodInvokerFactory for arguments count: " + method.getParameterCount());
      }
      return new MethodApi().route(route).context(context).method(method).options(options).methodInvokerFactory(factory);
    }).toList();
  }

  @SneakyThrows
  protected void registerRoute(Router router, MethodApi methodApi, ParameterProvider.Factory parameterProviderFactory, ResponseWriter responseWriter) {
    val options = methodApi.options();
    val methodInvoker = methodApi.methodInvokerFactory().create(methodApi.context(), methodApi.method(), parameterProviderFactory);
    val path = methodApi.route().value() + ((options.path() == null || options.path().isEmpty()) && options.methodAsPath() ? "/" + methodApi.method().getName().replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase(Locale.ROOT) : options.path());

    var route = router.route();

    for (OnRegisteringRoute onRegisteringRoute : onRegisteringRoutes) {
      route = onRegisteringRoute.before(router, path, methodApi);
    }

    route = route.path(path);

    if (options.pathRegex() != null && !options.pathRegex().isBlank()) {
      route = route.pathRegex(options.pathRegex());
    }

    if (options.method() != null) {
      route = route.method(HttpMethod.valueOf(options.method().name()));
    }

    if (options.consumes() != null) {
      for (String consume : options.consumes()) {
        route = route.consumes(consume);
      }
    }

    if (options.produces() != null) {
      for (String produce : options.produces()) {
        route = route.produces(produce);
      }
    }

    if (options.order() != null) {
      route = route.order(options.order());
    }

    if (!options.enable()) {
      route = route.disable();
    }

    if (options.virtualHost() != null) {
      route = route.virtualHost(options.virtualHost());
    }

    if (options.name() != null) {
      route = route.setName(options.name());
    }

    for (OnRegisteringRoute onRegisteringRoute : onRegisteringRoutes) {
      route = onRegisteringRoute.after(route, methodApi);
    }

    route.putMetadata(Constants.ROUTE_META_DATA_CLASS_METHOD_HANDLER, methodApi.context().getClass().getName() + " -> " + methodApi.method().getName());

    if (options.isBlocking()) {
      route.blockingHandler(ctx -> handleContext(ctx, methodInvoker, options, responseWriter), options.blockingOrdered());
      return;
    }

    route.handler(ctx -> handleContext(ctx, methodInvoker, options, responseWriter));
  }

  protected void handleContext(RoutingContext context, MethodInvoker methodInvoker, RouteOptions options, ResponseWriter responseWriter) {
    if (options.produces() != null) {
      context.response().putHeader(HttpHeaders.CONTENT_TYPE, String.join(";", options.produces()));
    }

    if (options.disableRequestLog()) {
      context.put(RoutingData.REQUEST_NO_LOG, true);
    }

    if (options.disableResponseLog()) {
      context.put(RoutingData.RESPONSE_NO_LOG, true);
    }

    if (options.noResponseWriter()) {
      return;
    }

    val result = methodInvoker.invoke(context, options);
    val response = context.response();
    if (response.ended()) {
      log.warn("Response already ended");
      return;
    }

    val written = responseWriter.write(context, result, options);

    if (!written && result != null) {
      throw new IllegalArgumentException("No response writer found for: " + result.getClass().getName());
    }

    if (!response.ended()) {
      if (result != null) {
        log.warn("Response written successfully, but RoutingContext not ended");
      }
      response.end();
    }
  }

}
