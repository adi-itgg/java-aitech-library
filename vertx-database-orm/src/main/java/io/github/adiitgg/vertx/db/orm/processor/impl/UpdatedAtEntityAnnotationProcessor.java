package io.github.adiitgg.vertx.db.orm.processor.impl;

import io.github.adiitgg.vertx.db.orm.annotation.UpdatedAt;
import io.github.adiitgg.vertx.db.orm.model.EntityFieldOptions;
import io.github.adiitgg.vertx.db.orm.processor.EntityAnnotationProcessor;
import io.github.adiitgg.vertx.db.orm.util.Utils;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;

public class UpdatedAtEntityAnnotationProcessor implements EntityAnnotationProcessor {

  @Override
  public boolean support(Annotation annotation) {
    return annotation instanceof UpdatedAt;
  }

  @Override
  public void process(Annotation annotation, EntityFieldOptions options) {
    options.isUpdatedAt(true);

    final MethodHandle now = Utils.findTimeStaticMHByType(options.fieldWrapper().type());
    options.defaultValueProvider(() -> {
      try {
        return now.invoke();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    });
  }

}
