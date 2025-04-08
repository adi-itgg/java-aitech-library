package io.aitech.vertx.db.orm.processor.impl;

import io.aitech.vertx.db.orm.model.EntityFieldOptions;
import io.aitech.vertx.db.orm.processor.EntityAnnotationProcessor;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;

@RequiredArgsConstructor
public class CompositeEntityAnnotationProcessor implements EntityAnnotationProcessor {

  private final EntityAnnotationProcessor[] entityAnnotationProcessors;

  @Override
  public boolean support(Annotation annotation) {
    for (final EntityAnnotationProcessor entityAnnotationProcessor : entityAnnotationProcessors) {
      if (entityAnnotationProcessor.support(annotation)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void process(Annotation annotation, EntityFieldOptions options) {
    for (final EntityAnnotationProcessor processor : entityAnnotationProcessors) {
      if (processor.support(annotation)) {
        processor.process(annotation, options);
      }
    }
  }

}
