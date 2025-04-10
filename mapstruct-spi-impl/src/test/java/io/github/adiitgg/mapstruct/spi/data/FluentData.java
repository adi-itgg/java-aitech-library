package io.github.adiitgg.mapstruct.spi.data;

public class FluentData {

  private String name;

  public String name() {
    return name;
  }

  public FluentData name(String name) {
    this.name = name;
    return this;
  }

}
