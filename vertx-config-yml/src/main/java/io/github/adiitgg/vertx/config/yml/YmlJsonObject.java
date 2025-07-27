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
   * @param k1 the mapping's key
   * @param v1 the mapping's value
   * @return a YmlJsonObject containing the specified mapping.
   */
  public static YmlJsonObject of(String k1, Object v1) {
    YmlJsonObject obj = new YmlJsonObject(new LinkedHashMap<>(1));

    obj.put(k1, v1);

    return obj;
  }

  /**
   * Create a YmlJsonObject containing two mappings.
   *
   * @param k1 the first mapping's key
   * @param v1 the first mapping's value
   * @param k2 the second mapping's key
   * @param v2 the second mapping's value
   * @return a YmlJsonObject containing the specified mappings.
   */
  public static YmlJsonObject of(String k1, Object v1, String k2, Object v2) {
    YmlJsonObject obj = new YmlJsonObject(new LinkedHashMap<>(2));

    obj.put(k1, v1);
    obj.put(k2, v2);

    return obj;
  }

  /**
   * Create a YmlJsonObject containing three mappings.
   *
   * @param k1 the first mapping's key
   * @param v1 the first mapping's value
   * @param k2 the second mapping's key
   * @param v2 the second mapping's value
   * @param k3 the third mapping's key
   * @param v3 the third mapping's value
   * @return a YmlJsonObject containing the specified mappings.
   */
  public static YmlJsonObject of(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
    YmlJsonObject obj = new YmlJsonObject(new LinkedHashMap<>(3));

    obj.put(k1, v1);
    obj.put(k2, v2);
    obj.put(k3, v3);

    return obj;
  }

  /**
   * Create a YmlJsonObject containing four mappings.
   *
   * @param k1 the first mapping's key
   * @param v1 the first mapping's value
   * @param k2 the second mapping's key
   * @param v2 the second mapping's value
   * @param k3 the third mapping's key
   * @param v3 the third mapping's value
   * @param k4 the fourth mapping's key
   * @param v4 the fourth mapping's value
   * @return a YmlJsonObject containing the specified mappings.
   */
  public static YmlJsonObject of(String k1, Object v1, String k2, Object v2, String k3, Object v3,
                              String k4, Object v4) {
    YmlJsonObject obj = new YmlJsonObject(new LinkedHashMap<>(4));

    obj.put(k1, v1);
    obj.put(k2, v2);
    obj.put(k3, v3);
    obj.put(k4, v4);

    return obj;
  }

  /**
   * Create a YmlJsonObject containing five mappings.
   *
   * @param k1 the first mapping's key
   * @param v1 the first mapping's value
   * @param k2 the second mapping's key
   * @param v2 the second mapping's value
   * @param k3 the third mapping's key
   * @param v3 the third mapping's value
   * @param k4 the fourth mapping's key
   * @param v4 the fourth mapping's value
   * @param k5 the fifth mapping's key
   * @param v5 the fifth mapping's value
   * @return a YmlJsonObject containing the specified mappings.
   */
  public static YmlJsonObject of(String k1, Object v1, String k2, Object v2, String k3, Object v3,
                              String k4, Object v4, String k5, Object v5) {
    YmlJsonObject obj = new YmlJsonObject(new LinkedHashMap<>(5));

    obj.put(k1, v1);
    obj.put(k2, v2);
    obj.put(k3, v3);
    obj.put(k4, v4);
    obj.put(k5, v5);

    return obj;
  }

  /**
   * Create a YmlJsonObject containing six mappings.
   *
   * @param k1 the first mapping's key
   * @param v1 the first mapping's value
   * @param k2 the second mapping's key
   * @param v2 the second mapping's value
   * @param k3 the third mapping's key
   * @param v3 the third mapping's value
   * @param k4 the fourth mapping's key
   * @param v4 the fourth mapping's value
   * @param k5 the fifth mapping's key
   * @param v5 the fifth mapping's value
   * @param k6 the sixth mapping's key
   * @param v6 the sixth mapping's value
   * @return a YmlJsonObject containing the specified mappings.
   */
  public static YmlJsonObject of(String k1, Object v1, String k2, Object v2, String k3, Object v3,
                              String k4, Object v4, String k5, Object v5, String k6, Object v6) {
    YmlJsonObject obj = new YmlJsonObject(new LinkedHashMap<>(6));

    obj.put(k1, v1);
    obj.put(k2, v2);
    obj.put(k3, v3);
    obj.put(k4, v4);
    obj.put(k5, v5);
    obj.put(k6, v6);

    return obj;
  }

  /**
   * Create a YmlJsonObject containing seven mappings.
   *
   * @param k1 the first mapping's key
   * @param v1 the first mapping's value
   * @param k2 the second mapping's key
   * @param v2 the second mapping's value
   * @param k3 the third mapping's key
   * @param v3 the third mapping's value
   * @param k4 the fourth mapping's key
   * @param v4 the fourth mapping's value
   * @param k5 the fifth mapping's key
   * @param v5 the fifth mapping's value
   * @param k6 the sixth mapping's key
   * @param v6 the sixth mapping's value
   * @param k7 the seventh mapping's key
   * @param v7 the seventh mapping's value
   * @return a YmlJsonObject containing the specified mappings.
   */
  public static YmlJsonObject of(String k1, Object v1, String k2, Object v2, String k3, Object v3,
                              String k4, Object v4, String k5, Object v5, String k6, Object v6,
                              String k7, Object v7) {
    YmlJsonObject obj = new YmlJsonObject(new LinkedHashMap<>(7));

    obj.put(k1, v1);
    obj.put(k2, v2);
    obj.put(k3, v3);
    obj.put(k4, v4);
    obj.put(k5, v5);
    obj.put(k6, v6);
    obj.put(k7, v7);

    return obj;
  }

  /**
   * Create a YmlJsonObject containing eight mappings.
   *
   * @param k1 the first mapping's key
   * @param v1 the first mapping's value
   * @param k2 the second mapping's key
   * @param v2 the second mapping's value
   * @param k3 the third mapping's key
   * @param v3 the third mapping's value
   * @param k4 the fourth mapping's key
   * @param v4 the fourth mapping's value
   * @param k5 the fifth mapping's key
   * @param v5 the fifth mapping's value
   * @param k6 the sixth mapping's key
   * @param v6 the sixth mapping's value
   * @param k7 the seventh mapping's key
   * @param v7 the seventh mapping's value
   * @param k8 the eighth mapping's key
   * @param v8 the eighth mapping's value
   * @return a YmlJsonObject containing the specified mappings.
   */
  public static YmlJsonObject of(String k1, Object v1, String k2, Object v2, String k3, Object v3,
                              String k4, Object v4, String k5, Object v5, String k6, Object v6,
                              String k7, Object v7, String k8, Object v8) {
    YmlJsonObject obj = new YmlJsonObject(new LinkedHashMap<>(8));

    obj.put(k1, v1);
    obj.put(k2, v2);
    obj.put(k3, v3);
    obj.put(k4, v4);
    obj.put(k5, v5);
    obj.put(k6, v6);
    obj.put(k7, v7);
    obj.put(k8, v8);

    return obj;
  }

  /**
   * Create a YmlJsonObject containing nine mappings.
   *
   * @param k1 the first mapping's key
   * @param v1 the first mapping's value
   * @param k2 the second mapping's key
   * @param v2 the second mapping's value
   * @param k3 the third mapping's key
   * @param v3 the third mapping's value
   * @param k4 the fourth mapping's key
   * @param v4 the fourth mapping's value
   * @param k5 the fifth mapping's key
   * @param v5 the fifth mapping's value
   * @param k6 the sixth mapping's key
   * @param v6 the sixth mapping's value
   * @param k7 the seventh mapping's key
   * @param v7 the seventh mapping's value
   * @param k8 the eighth mapping's key
   * @param v8 the eighth mapping's value
   * @param k9 the ninth mapping's key
   * @param v9 the ninth mapping's value
   * @return a YmlJsonObject containing the specified mappings.
   */
  public static YmlJsonObject of(String k1, Object v1, String k2, Object v2, String k3, Object v3,
                              String k4, Object v4, String k5, Object v5, String k6, Object v6,
                              String k7, Object v7, String k8, Object v8, String k9, Object v9) {
    YmlJsonObject obj = new YmlJsonObject(new LinkedHashMap<>(9));

    obj.put(k1, v1);
    obj.put(k2, v2);
    obj.put(k3, v3);
    obj.put(k4, v4);
    obj.put(k5, v5);
    obj.put(k6, v6);
    obj.put(k7, v7);
    obj.put(k8, v8);
    obj.put(k9, v9);

    return obj;
  }

  /**
   * Create a YmlJsonObject containing ten mappings.
   *
   * @param k1 the first mapping's key
   * @param v1 the first mapping's value
   * @param k2 the second mapping's key
   * @param v2 the second mapping's value
   * @param k3 the third mapping's key
   * @param v3 the third mapping's value
   * @param k4 the fourth mapping's key
   * @param v4 the fourth mapping's value
   * @param k5 the fifth mapping's key
   * @param v5 the fifth mapping's value
   * @param k6 the sixth mapping's key
   * @param v6 the sixth mapping's value
   * @param k7 the seventh mapping's key
   * @param v7 the seventh mapping's value
   * @param k8 the eighth mapping's key
   * @param v8 the eighth mapping's value
   * @param k9 the ninth mapping's key
   * @param v9 the ninth mapping's value
   * @param k10 the tenth mapping's key
   * @param v10 the tenth mapping's value
   * @return a YmlJsonObject containing the specified mappings.
   */
  public static YmlJsonObject of(String k1, Object v1, String k2, Object v2, String k3, Object v3,
                              String k4, Object v4, String k5, Object v5, String k6, Object v6,
                              String k7, Object v7, String k8, Object v8, String k9, Object v9,
                              String k10, Object v10) {
    YmlJsonObject obj = new YmlJsonObject(new LinkedHashMap<>(10));

    obj.put(k1, v1);
    obj.put(k2, v2);
    obj.put(k3, v3);
    obj.put(k4, v4);
    obj.put(k5, v5);
    obj.put(k6, v6);
    obj.put(k7, v7);
    obj.put(k8, v8);
    obj.put(k9, v9);
    obj.put(k10, v10);

    return obj;
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

  public YmlJsonObject getYmlJsonObject(String key, YmlJsonObject def) {
    return YmlJsonObject.of(super.getJsonObject(key, def));
  }


}
