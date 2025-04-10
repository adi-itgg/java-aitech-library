package io.github.adiitgg.vertx.http.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class RouteOptions {

  private String path;
  private String pathRegex;
  private Method method;
  private String[] consumes;
  private String[] produces;
  private Integer order;
  private boolean enable = true;
  private String virtualHost;
  private String name;
  private boolean auth;
  private boolean noResponseWriter;
  private boolean methodAsPath = true;
  private boolean isBlocking;
  private boolean blockingOrdered;
  private Map<String, Object> metaData;


  public Map<String, Object> metaData() {
    if (this.metaData == null) {
      this.metaData = new HashMap<>();
    }
    return metaData;
  }

  public RouteOptions put(String key, Object value) {
    metaData().put(key, value);
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T> T getOrDefault(String key, T defaultValue) {
    return (T) metaData().getOrDefault(key, defaultValue);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    return (T) metaData().get(key);
  }

}
