package io.aitech.vertx.db.orm.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(fluent = true)
public class QueryPreparer {

  private String sql;
  private List<EntityFieldOptions> fieldOptions;
  private List<FieldWrapper> fieldWrappers; // for update current entity with new value

}
