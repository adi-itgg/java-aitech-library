package io.github.adiitgg.vertx.db.orm.model;

public enum DAOQueryType {
  INSERT,
  UPDATE,
  UPSERT;

  public static final DAOQueryType[] VALUES = values();

}
