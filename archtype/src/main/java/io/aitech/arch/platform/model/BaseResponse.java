package io.aitech.arch.platform.model;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@NoArgsConstructor
public class BaseResponse {

  private String code;
  private String message;
  private Object data;


  public Buffer toBuffer() {
    return Json.encodeToBuffer(this);
  }

}
