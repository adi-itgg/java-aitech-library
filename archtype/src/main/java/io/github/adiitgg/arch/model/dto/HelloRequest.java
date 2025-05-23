package io.github.adiitgg.arch.model.dto;

import io.github.adiitgg.arch.model.HelloType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HelloRequest(
  @NotBlank
  @Size(min = 1, max = 255)
  String name,
  @NotNull
  HelloType type
) {
}
