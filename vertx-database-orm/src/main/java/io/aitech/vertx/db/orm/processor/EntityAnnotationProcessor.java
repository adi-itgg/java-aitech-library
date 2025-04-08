package io.aitech.vertx.db.orm.processor;

import io.aitech.vertx.db.orm.model.EntityFieldOptions;

import java.lang.annotation.Annotation;

public interface EntityAnnotationProcessor {

  boolean support(Annotation annotation);

  void process(Annotation annotation, EntityFieldOptions options);

}
