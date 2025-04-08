package io.aitech.vertx.config.yml;

import io.aitech.vertx.config.yml.parser.TypeParser;
import io.aitech.vertx.config.yml.parser.impl.*;
import io.vertx.config.spi.ConfigProcessor;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import lombok.val;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class YmlProcessor implements ConfigProcessor {

  private static final String ENABLE_CUSTOM_ENV = "enable.custom.env";
  private final Pattern customEnvPattern = Pattern.compile("\\$\\{(.*?)\\|(.*?)}");
  private final TypeParser typeParser;

  public YmlProcessor() {
    // Default parser
    val typeParsers = new ArrayList<TypeParser>();
    typeParsers.add(new BigDecimalTypeParser());
    typeParsers.add(new JsonObjectTypeParser());
    typeParsers.add(new JsonArrayTypeParser());
    typeParsers.add(new BooleanTypeParser());

    this.typeParser = new CompositeTypeParser(typeParsers);
  }

  @Override
  public String name() {
    return "yml";
  }

  @Override
  public Future<JsonObject> process(Vertx vertx, JsonObject configuration, Buffer input) {
    if (input.length() == 0) {
      // the parser does not support empty files, which should be managed to be homogeneous
      return ((ContextInternal) vertx.getOrCreateContext()).succeededFuture(new JsonObject());
    }

    val config = Objects.requireNonNullElse(configuration, JsonObject.of());
    val configYmlLoader = fromJson(config.getJsonObject("yaml"));
    val enableCustomEnv = config.getBoolean(ENABLE_CUSTOM_ENV, true);

    // Use executeBlocking even if the bytes are in memory
    return vertx.executeBlocking(() -> {
      try {
        final Yaml yamlMapper = new Yaml(new SafeConstructor(configYmlLoader));
        Map<String, Object> doc = yamlMapper.load(input.toString(StandardCharsets.UTF_8));
        val propPrefix = (String) doc.getOrDefault("prop-prefix", "");
        val envPrefix = (String) doc.getOrDefault("env-prefix", "");
        return jsonify(propPrefix, envPrefix, enableCustomEnv, doc);
      } catch (ClassCastException e) {
        throw new DecodeException("Failed to decode YAML", e);
      }
    });
  }

  /**
   * Yaml allows map keys of type object, however json always requires key as String,
   * this helper method will ensure we adapt keys to the right type
   *
   * @param yaml yaml map
   * @return json map
   */
  @SuppressWarnings("unchecked")
  private JsonObject jsonify(String propPath, String envPath, Boolean enableCustomEnv, Map<String, Object> yaml) {
    if (yaml == null) {
      return null;
    }

    val json = new JsonObject();
    for (Map.Entry<String, Object> kv : yaml.entrySet()) {
      final String key = kv.getKey();

      final String prop = (propPath + ((propPath.isEmpty() ? "" : ".") + key).toLowerCase()); // for best practice props require lowercase!
      final String env = (envPath + ((envPath.isEmpty() ? "" : "_") + key)).replaceAll("[^a-zA-Z0-9_]", ""); // for best practice env requires underscore
      Object value = kv.getValue();
      if (value instanceof Map) {
        value = jsonify(prop, env, enableCustomEnv, (Map<String, Object>) value);
      }
      // snake yaml handles dates as java.util.Date, and JSON does Instant
      if (value instanceof Date) {
        value = ((Date) value).toInstant();
      }
      json.put(key, value);

      String envValue = System.getenv(env);
      if (envValue == null) {
        envValue = System.getenv(env.toUpperCase());
      }
      if (envValue != null) {
        json.put(key, parse(envValue));
      }

      String propValue = System.getProperty(prop);
      if (propValue != null) {
        json.put(key, parse(propValue));
      }

      if (enableCustomEnv && value instanceof String) {
        val matcher = customEnvPattern.matcher((String) value);
        if (matcher.find()) {
          val envKey = matcher.group(1).trim();
          val defaultValue = matcher.group(2).trim();
          if (defaultValue.equals("null")) {
            json.put(key, parse(System.getenv(envKey)));
          } else {
            json.put(key, parse(Objects.requireNonNullElse(System.getenv(envKey), defaultValue)));
          }
        }
      }

    }

    return json;
  }

  private Object parse(String value) {
    if (value == null || value.equals("null")) {
      return null;
    }
    if (value.length() > 1) {
      if ((value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') || (value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'')) {
        return parse(value.substring(1, value.length() - 1));
      }
    }
    return typeParser.parse(value);
  }


  private LoaderOptions fromJson(JsonObject json) {
    val loaderOptions = new LoaderOptions();
    if (json == null) {
      return loaderOptions;
    }

    for (val member : json) {
      switch (member.getKey()) {
        case "allowDuplicateKeys" -> {
          if (member.getValue() instanceof Boolean v) {
            loaderOptions.setAllowDuplicateKeys(v);
          }
        }
        case "wrappedToRootException" -> {
          if (member.getValue() instanceof Boolean v) {
            loaderOptions.setWrappedToRootException(v);
          }
        }
        case "maxAliasesForCollections" -> {
          if (member.getValue() instanceof Number v) {
            loaderOptions.setMaxAliasesForCollections(v.intValue());
          }
        }
        case "allowRecursiveKeys" -> {
          if (member.getValue() instanceof Boolean v) {
            loaderOptions.setAllowRecursiveKeys(v);
          }
        }
        case "processComments" -> {
          if (member.getValue() instanceof Boolean v) {
            loaderOptions.setProcessComments(v);
          }
        }
        case "enumCaseSensitive" -> {
          if (member.getValue() instanceof Boolean v) {
            loaderOptions.setEnumCaseSensitive(v);
          }
        }
        case "nestingDepthLimit" -> {
          if (member.getValue() instanceof Number v) {
            loaderOptions.setNestingDepthLimit(v.intValue());
          }
        }
        case "codePointLimit" -> {
          if (member.getValue() instanceof Number v) {
            loaderOptions.setCodePointLimit(v.intValue());
          }
        }
      }
    }

    return loaderOptions;
  }

}
