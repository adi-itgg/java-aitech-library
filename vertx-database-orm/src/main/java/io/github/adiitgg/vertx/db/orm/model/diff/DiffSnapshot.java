package io.github.adiitgg.vertx.db.orm.model.diff;

import io.github.adiitgg.vertx.db.orm.model.EntityOptions;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(fluent = true)
public class DiffSnapshot {

  private EntityOptions entityOptions;
  private List<Object> values;

}
