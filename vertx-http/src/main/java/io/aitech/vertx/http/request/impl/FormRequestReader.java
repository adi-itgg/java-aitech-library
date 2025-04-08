package io.aitech.vertx.http.request.impl;

import io.aitech.vertx.http.model.ParamType;
import io.aitech.vertx.http.request.RequestReader;
import io.aitech.vertx.http.util.StringUtil;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
public class FormRequestReader implements RequestReader {

  private final DatabindCodec databindCodec;

  @Override
  public boolean isSupported(RoutingContext context, Type type, ParamType paramType) {
    return !context.request().formAttributes().isEmpty() && paramType == ParamType.FORM;
  }

  @Override
  public Object read(RoutingContext context, Type type, ParamType paramType) {
    Map<String, String> formData = new HashMap<>();
    for (Map.Entry<String, String> data : context.request().formAttributes()) {
      formData.put(StringUtil.toCamelCase(data.getKey().toLowerCase(Locale.ROOT), '-'), data.getValue());
    }
    return databindCodec.fromValue(formData, (Class<?>) type);
  }

}
