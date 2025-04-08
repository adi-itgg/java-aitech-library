package io.aitech.vertx.db.orm.processor.impl;

import io.aitech.vertx.db.orm.annotation.Serialized;
import io.aitech.vertx.db.orm.model.EntityFieldOptions;
import io.aitech.vertx.db.orm.processor.EntityAnnotationProcessor;

import java.lang.annotation.Annotation;

public class SerializedEntityAnnotationProcessor implements EntityAnnotationProcessor {

  @Override
  public boolean support(Annotation annotation) {
    return annotation instanceof Serialized;
  }

  @Override
  public void process(Annotation annotation, EntityFieldOptions options) {
    final Serialized serialized = (Serialized) annotation;
    options.serialized(serialized.value());
  }

}
