package io.github.adiitgg.vertx.config.impl;

import io.github.adiitgg.vertx.config.ConfigRetrieverExtended;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.config.impl.ConfigRetrieverImpl;
import io.vertx.core.Closeable;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.impl.VertxImpl;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigRetrieverExtendedImpl extends ConfigRetrieverImpl implements ConfigRetrieverExtended, Closeable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRetrieverExtended.class);
  private static final String DEFAULT_CONFIG_DIR_PATH = "config";
  private static final String DEFAULT_CONFIG_PATH = DEFAULT_CONFIG_DIR_PATH + File.separator + "config.";
  private static final JsonObject configExtensions = JsonObject.of(
    "hocon", "conf"
  );

  private final VertxImpl vertx;
  private final ConfigRetrieverOptions extendedOptions;
  private final ContextInternal context;
  private boolean isClosed;
  private ClassLoader classLoader;
  private String[] args = new String[0];
  private String format = "yml";
  private JsonObject config;

  public ConfigRetrieverExtendedImpl(Vertx vertx, ConfigRetrieverOptions extendedOptions) {
    super(vertx, extendedOptions);
    this.vertx = (VertxImpl) vertx;
    this.extendedOptions = extendedOptions;
    this.context = (ContextInternal) vertx.getOrCreateContext();

    this.vertx.addCloseHook(this);
  }

  public ConfigRetrieverExtendedImpl(Vertx vertx) {
    this(vertx, (new ConfigRetrieverOptions()).setIncludeDefaultStores(true).setScanPeriod(-1L));
  }


  @Override
  public void close(Promise<Void> promise) {
    close();
    promise.tryComplete();
  }

  @Override
  public synchronized void close() {
    if (!this.isClosed) {
      this.isClosed = true;
      super.close();
    }
  }

  private List<String> parseArgs(String[] args) {
    final String[] profiles;
    if (args != null && args.length > 0) {
      profiles = Arrays.stream(args).filter(arg -> arg.toLowerCase().startsWith("--profiles=") || arg.toLowerCase().startsWith("-p="))
        .findFirst()
        .map(p -> (p.startsWith("-p=") ? p.substring(3) : p.substring("--profiles=".length())).split(","))
        .orElse(getDefaultProfile().split(","));
    } else {
      profiles = getDefaultProfile().split(",");
    }
    LOGGER.info("argument config active profiles: " + String.join(", ", profiles));
    return Arrays.asList(profiles);
  }

  @SneakyThrows
  private List<String> getResources(ClassLoader classLoader, String dirPath, Predicate<String> filter) {
    if (classLoader == null) {
      throw new IllegalArgumentException("classLoader must be not null!");
    }

    final URL url;
    if (dirPath == null) {
      url = classLoader.getResource("");
    } else {
      dirPath = dirPath.replace(context.owner().resolveFile("").getAbsolutePath() + File.separator, "");
      url = classLoader.getResource(dirPath);
    }
    if (url == null) {
      throw new FileSystemNotFoundException("Directory path not exists! - " + dirPath);
    }

    try (Stream<Path> stream = Files.list(Paths.get(url.toURI()))) {
      final String finalDirPath = dirPath;

      return stream.filter(f -> !Files.isDirectory(f) && filter.test(f.getFileName().toString()))
        .map(f -> (finalDirPath == null ? "" : finalDirPath + File.separator) + f.getFileName().toString())
        .toList();
    } catch (FileSystemNotFoundException e) {
      final String executablePath = ConfigRetrieverExtended.class.getProtectionDomain().getCodeSource().getLocation().getPath();
      try (JarFile jarFile = new JarFile(executablePath)) {
        final Enumeration<JarEntry> entries = jarFile.entries();

        final List<String> filenames = new ArrayList<>();
        while (entries.hasMoreElements()) {
          final JarEntry entry = entries.nextElement();
          final String entryName = entry.getName();
          if (filter.test(entryName)) {
            filenames.add(entryName);
          }
        }
        return filenames;
      }
    }
  }

  private String getDefaultConfigPath(String fileSuffix) {
    String value = System.getenv("VERTX_CONFIG_PATH");
    if (value == null || value.trim().isEmpty()) {
      value = System.getProperty("vertx-config-path");
    }
    if (value != null && !value.trim().isEmpty()) {
      return value.trim();
    }
    File file = context.owner().resolveFile(DEFAULT_CONFIG_PATH + fileSuffix);
    boolean exists = file != null && file.exists();
    if (exists) {
      return file.getAbsolutePath();
    }
    file = context.owner().resolveFile(new File(DEFAULT_CONFIG_PATH + fileSuffix).getName());
    exists = file != null && file.exists();
    if (exists) {
      return file.getAbsolutePath();
    }
    return null;
  }

  private String getDefaultDirPath() {
    String value = System.getenv("VERTX_CONFIG_DIR_PATH");
    if (value == null || value.trim().isEmpty()) {
      value = System.getProperty("vertx-config-dir-path");
    }
    if (value != null && !value.trim().isEmpty()) {
      return value.trim();
    }
    File file = context.owner().resolveFile(DEFAULT_CONFIG_DIR_PATH);
    boolean exists = file != null && file.exists();
    if (exists) {
      return file.getAbsolutePath();
    }
    return null;
  }

  private String getDefaultProfile() {
    String value = System.getenv("VERTX_PROFILES");
    if (value == null || value.trim().isEmpty()) {
      value = System.getProperty("vertx-profiles");
    }
    if (value != null) {
      return value.trim();
    }
    return "";
  }


  @Override
  public ConfigRetrieverExtendedImpl classLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
    return this;
  }

  @Override
  public ConfigRetrieverExtendedImpl args(String[] args) {
    this.args = args;
    return this;
  }

  @Override
  public ConfigRetrieverExtendedImpl format(String format) {
    if (format == null) {
      throw new IllegalArgumentException("format must be not null!");
    }
    this.format = format;
    return this;
  }

  @Override
  public ConfigRetrieverExtendedImpl config(JsonObject config) {
    if (config == null) {
      throw new IllegalArgumentException("config must be not null!");
    }
    this.config = config;
    return this;
  }

  @SneakyThrows
  @Override
  public ConfigRetrieverExtendedImpl build() {
    final String fileSuffix = configExtensions.containsKey(format) ? configExtensions.getString(format) : format;
    final List<String> profiles = parseArgs(args);
    final String configDirPath = getDefaultDirPath();
    final Predicate<String> filter = filename -> filename.startsWith("config") && filename.endsWith("." + fileSuffix) && !filename.equals("config." + fileSuffix);

    // get file from resources
    List<String> configFiles = getResources(classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader, configDirPath, filter);
    LOGGER.info("available config files: " + configFiles.stream().map(s -> new File(s).getName()).collect(Collectors.joining(", ")));

    List<String> prepareConfigFiles = configFiles.stream()
      .filter(filename -> !filename.contains("-") || profiles.stream().anyMatch(arg -> filename.contains("config-" + arg + "." + fileSuffix)))
      .toList();

    // make override config file sorted by passing args
    List<ConfigStoreOptions> configStores = profiles.stream()
      .map(p -> prepareConfigFiles.stream().filter(f -> f.endsWith("config-" + p + "." + fileSuffix)).findFirst().orElse(null))
      .filter(Objects::nonNull)
      .map(filename -> new ConfigStoreOptions()
        .setType("file")
        .setFormat(format)
        .setConfig(config == null ? JsonObject.of("path", filename) : JsonObject.of("path", filename).mergeIn(config))
      ).toList();

    // if not found, use default (possible migration from old ConfigRetrieverImpl)
    ConfigRetrieverOptions extendedOptions = this.extendedOptions != null ? this.extendedOptions : new ConfigRetrieverOptions()
      .setIncludeDefaultStores(true)
      .setScanPeriod(-1L);

    String defaultConfigPath = getDefaultConfigPath(fileSuffix);
    if (defaultConfigPath != null) {
      extendedOptions.addStore(new ConfigStoreOptions()
        .setType("file")
        .setFormat(format)
        .setConfig(config == null ? JsonObject.of("path", defaultConfigPath) : JsonObject.of("path", defaultConfigPath).mergeIn(config))
      );
    }
    extendedOptions.getStores().addAll(configStores);

    // close current config retriever
    this.close();
    this.isClosed = false;

    // create new config retriever with extended options
    ConfigRetrieverExtended configRetrieverExtended = ConfigRetrieverExtended.create(vertx, extendedOptions);

    // update current options
    Field optionsField = ConfigRetrieverImpl.class.getDeclaredField("options");
    if (optionsField.trySetAccessible()) {
      optionsField.set(this, optionsField.get(configRetrieverExtended));
    }

    // update current providers
    Field providers = ConfigRetrieverImpl.class.getDeclaredField("providers");
    if (providers.trySetAccessible()) {
      providers.set(this, providers.get(configRetrieverExtended));
    }

    // update current streamOfConfiguration
    Field streamOfConfigurationField = ConfigRetrieverImpl.class.getDeclaredField("streamOfConfiguration");
    if (streamOfConfigurationField.trySetAccessible()) {
      streamOfConfigurationField.set(this, streamOfConfigurationField.get(configRetrieverExtended));
    }

    String loadedConfigs = extendedOptions.getStores().stream()
      .map(o -> new File(o.getConfig().getString("path")).getName())
      .collect(Collectors.joining(", "));
    LOGGER.info("config files loaded: " + loadedConfigs);
    return this;
  }

}
