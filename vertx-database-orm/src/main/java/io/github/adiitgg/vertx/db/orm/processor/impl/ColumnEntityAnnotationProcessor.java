package io.github.adiitgg.vertx.db.orm.processor.impl;

import io.github.adiitgg.vertx.db.orm.annotation.Column;
import io.github.adiitgg.vertx.db.orm.model.EntityFieldOptions;
import io.github.adiitgg.vertx.db.orm.processor.EntityAnnotationProcessor;

import java.lang.annotation.Annotation;

public class ColumnEntityAnnotationProcessor implements EntityAnnotationProcessor {

  @Override
  public boolean support(Annotation annotation) {
    return annotation instanceof Column;
  }

  @Override
  public void process(Annotation annotation, EntityFieldOptions options) {
    Column column = (Column) annotation;
    options.columnName(column.name());
    options.length(column.length());
    options.nullable(column.nullable());
    options.insertable(column.insertable());
    options.updateable(column.updatable());
    options.canConflict(column.canConflict());
  }

}
