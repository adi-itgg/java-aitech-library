package io.github.adiitgg.vertx.db.orm.impl;

import io.github.adiitgg.vertx.db.orm.annotation.Entity;
import io.github.adiitgg.vertx.db.orm.model.EntityFieldOptions;
import io.github.adiitgg.vertx.db.orm.model.FieldWrapper;
import io.github.adiitgg.vertx.db.orm.processor.*;
import io.github.adiitgg.vertx.db.orm.processor.impl.*;
import io.github.adiitgg.vertx.db.orm.util.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AnnotationPersistence {

  private final EntityAnnotationProcessor entityAnnotationProcessor;

  public AnnotationPersistence() {
    List<EntityAnnotationProcessor> processors = new ArrayList<>();

    processors.add(new IdEntityAnnotationProcessor());
    processors.add(new ColumnEntityAnnotationProcessor());
    processors.add(new CreatedAtEntityAnnotationProcessor());
    processors.add(new UpdatedAtEntityAnnotationProcessor());
    processors.add(new SerializedEntityAnnotationProcessor());

    this.entityAnnotationProcessor = new CompositeEntityAnnotationProcessor(processors.toArray(new EntityAnnotationProcessor[0]));
  }


  public EntityFieldOptions createEntityFieldOptions(Field field) {
    EntityFieldOptions options = new EntityFieldOptions();
    FieldWrapper fieldWrapper = new FieldWrapper();

    fieldWrapper.name(field.getName());
    fieldWrapper.type(field.getType());

    options.fieldWrapper(fieldWrapper);

    if (!field.trySetAccessible()) {
      return null;
    }

    fieldWrapper.getter(Utils.createGetter(field));
    fieldWrapper.setter(Utils.createSetter(field));

    for (Annotation annotation : field.getAnnotations()) {
      entityAnnotationProcessor.process(annotation, options);
    }
    if (options.columnName() == null || options.columnName().isBlank()) {
      String defaultColumnName = Utils.toSnakeCase(field.getName());
      options.columnName(defaultColumnName);
    }
    fieldWrapper.columnName(options.columnName());
    return options;
  }

  public String getEntityName(Class<?> clazz) {
    Entity entityAnnotation = clazz.getAnnotation(Entity.class);

    if (entityAnnotation != null && !entityAnnotation.name().isBlank()) {
      return entityAnnotation.name();
    }
    return clazz.getSimpleName().toLowerCase(Locale.ROOT);
  }

}
