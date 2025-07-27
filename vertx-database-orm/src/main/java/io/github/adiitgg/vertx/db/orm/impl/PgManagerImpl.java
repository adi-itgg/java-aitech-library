package io.github.adiitgg.vertx.db.orm.impl;

import io.github.adiitgg.vertx.db.orm.PgManager;
import io.github.adiitgg.vertx.db.orm.PgModule;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class PgManagerImpl implements PgManager {

  private List<PgModule> loadedModules;

  private static class Holder {
    private static final PgManagerImpl INSTANCE = new PgManagerImpl();
  }

  public static PgManagerImpl getInstance() {
    return Holder.INSTANCE;
  }

  @Override
  public void findAndLoadModules(ClassLoader classLoader) {
    loadedModules = findModules(classLoader);
  }

  @Override
  public List<PgModule> findModules(ClassLoader classLoader) {
    ArrayList<PgModule> modules = new ArrayList<>();
    ServiceLoader<PgModule> loader = secureGetServiceLoader(PgModule.class, classLoader);
    for (PgModule module : loader) {
      modules.add(module);
    }
    return modules;
  }

  @Override
  public List<PgModule> getLoadedModules() {
    return loadedModules;
  }

  @SuppressWarnings("removal")
  private <T> ServiceLoader<T> secureGetServiceLoader(final Class<T> clazz, final ClassLoader classLoader) {
    final SecurityManager sm = System.getSecurityManager();
    if (sm == null) {
      return (classLoader == null) ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
    }
    return AccessController.doPrivileged((PrivilegedAction<ServiceLoader<T>>) () -> (classLoader == null) ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader));
  }

}
