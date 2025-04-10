package io.github.adiitgg.vertx.config.yml;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

public class YmlJsonObject extends JsonObject {


  private static final WeakHashMap<JsonObject, YmlJsonObject> cachedJsonObject = new WeakHashMap<>();

  public YmlJsonObject(String json) {
    super(json);
    readAndLoadAllKeys();
  }

  public YmlJsonObject() {
    super();
    readAndLoadAllKeys();
  }

  public YmlJsonObject(Map<String, Object> map) {
    super(map);
    readAndLoadAllKeys();
  }

  public YmlJsonObject(Buffer buf) {
    super(buf);
    readAndLoadAllKeys();
  }

  /**
   * Creates a new YmlJsonObject instance from a JsonObject, or retrieves a cached instance if available.
   *
   * @param  jsonObject  the JsonObject to create a YmlJsonObject instance from
   * @return             the created or cached YmlJsonObject instance
   */
  public static YmlJsonObject of(JsonObject jsonObject) {
    YmlJsonObject instance = cachedJsonObject.get(jsonObject);
    if (instance == null &&  jsonObject != null) {
      instance = new YmlJsonObject(jsonObject.getMap());
      cachedJsonObject.put(jsonObject, instance);
    }
    return instance;
  }


  private void readAndLoadAllKeys() {
    loadAllKeys("", this);
  }

  private void loadAllKeys(String path, JsonObject jo) {
    for (String fieldName : new ArrayList<>(jo.fieldNames())) {
      Object value = jo.getValue(fieldName);
      String key = path.isEmpty() ? fieldName : path + "." + fieldName;
      getMap().put(key, value);
      if (value instanceof JsonObject) {
        loadAllKeys(key, (JsonObject) value);
      }
    }
  }


  /**
   * Create a YmlJsonObject containing zero mappings.
   *
   * @return an empty YmlJsonObject.
   */
  public static YmlJsonObject of() {
    return new YmlJsonObject();
  }

  /**
   * Create a YmlJsonObject containing a single mapping.
   *
   * @param kv the mapping's key (String) & value (Object)
   * @return a YmlJsonObject containing the specified mapping.
   */
  public static YmlJsonObject of(Object ...kv) {
    YmlJsonObject obj = new YmlJsonObject(new LinkedHashMap<>(kv.length));

    if (kv.length % 2 != 0) {
      throw new IllegalArgumentException("Invalid number of arguments");
    }

    for (int i = 0; i < kv.length; i++) {
      obj.put((String) kv[i], kv[i + 1]);
      i++;
    }

    return obj;
  }

  /**
   * Create a YmlJsonObject from the fields of a Java object.
   * Faster than calling `new YmlJsonObject(Json.encode(obj))`.
   * <p/
   * Returns {@code null} when {@code obj} is {@code null}.
   *
   * @param obj The object to convert to a YmlJsonObject.
   * @throws IllegalArgumentException if conversion fails due to an incompatible type.
   */
  @SuppressWarnings("unchecked")
  public static YmlJsonObject mapFrom(Object obj) {
    if (obj == null) {
      return null;
    } else {
      return new YmlJsonObject((Map<String, Object>) Json.CODEC.fromValue(obj, Map.class));
    }
  }

  /**
   * use this instead getJsonObject()
   * @return this
   */
  @Deprecated
  public JsonObject getJsonObject() {
    return this;
  }


  public YmlJsonObject putNull(String key) {
    super.putNull(key);
    return setYmlValue(key, null);
  }

  public YmlJsonObject put(String key, Object value) {
    super.put(key, value);
    return setYmlValue(key, value);
  }

  @Override
  public Object remove(String key) {
    Object result = super.remove(key);
    Object result2 = setYmlValue(key, null, true);
    return result == null ? result2 : result;
  }

  private YmlJsonObject setYmlValue(String path, Object value) {
    return (YmlJsonObject) setYmlValue(path, value, false);
  }
  private Object setYmlValue(String path, Object value, boolean isRemove) {
    String[] sp = path.split("\\.");
    if (sp.length == 0) {
      if (isRemove) {
        return super.remove(path);
      }
      super.put(path, value);
      return this;
    }
    JsonObject temp = this;
    for (int i = 0; i < sp.length; i++) {
      String key = sp[i];
      if ((i + 1) == sp.length) {
        if (isRemove) {
          return temp.getMap().remove(key);
        }
        temp.getMap().put(key, value);
        return this;
      }
      JsonObject vo = temp.getJsonObject(key);
      if (vo == null) {
        vo = YmlJsonObject.of();
      }
      temp.getMap().put(key, vo);
      temp = vo;
    }
    return isRemove ? null : this;
  }

  public YmlJsonObject getJsonObject(String key) {
    return YmlJsonObject.of(super.getJsonObject(key));
  }
  public YmlJsonObject getJsonObject(String key, YmlJsonObject def) {
    return YmlJsonObject.of(super.getJsonObject(key, def));
  }

  public YmlJsonObject mergeIn(YmlJsonObject other) {
    return YmlJsonObject.of(super.mergeIn(other));
  }

  public YmlJsonObject mergeIn(YmlJsonObject other, boolean deep) {
    return YmlJsonObject.of(super.mergeIn(other, deep));
  }

  public YmlJsonObject mergeIn(YmlJsonObject other, int depth) {
    return YmlJsonObject.of(super.mergeIn(other, depth));
  }

  public YmlJsonObject copy() {
    return YmlJsonObject.of(super.copy());
  }

  public YmlJsonObject copy(Function<Object, ?> cloner) {
    return YmlJsonObject.of(super.copy(cloner));
  }

  public YmlJsonObject getYmlJsonObject(String key) {
    return YmlJsonObject.of(super.getJsonObject(key));
  }

  public YmlJsonObject getYmlJsonObject(String key, JsonObject def) {
    return YmlJsonObject.of(super.getJsonObject(key, def));
  }


}
