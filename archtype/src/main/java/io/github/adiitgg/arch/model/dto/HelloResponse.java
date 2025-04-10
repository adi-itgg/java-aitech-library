package io.github.adiitgg.arch.model.dto;

import io.github.adiitgg.arch.model.HelloType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class HelloResponse {

  private String name;
  private HelloType type;

}
