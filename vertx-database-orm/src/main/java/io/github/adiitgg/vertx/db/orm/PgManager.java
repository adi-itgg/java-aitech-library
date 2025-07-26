package io.github.adiitgg.vertx.db.orm;

import io.github.adiitgg.vertx.db.orm.impl.PgManagerImpl;

import java.util.List;

public interface PgManager {

  static PgManager getInstance() {
    return PgManagerImpl.getInstance();
  }

  void findAndLoadModules(ClassLoader classLoader);

  List<PgModule> findModules(ClassLoader classLoader);

  List<PgModule> getLoadedModules();
}
