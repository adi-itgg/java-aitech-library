package io.aitech.arch.model.dto;

import io.aitech.arch.model.HelloType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class HelloResponse {
  
  private String name;
  private HelloType type;
  
}
