package io.github.adiitgg.arch.model.mapper;

import io.github.adiitgg.arch.model.dto.HelloRequest;
import io.github.adiitgg.arch.model.dto.HelloResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jsr330")
public interface HelloMapper {

  HelloResponse toResponse(HelloRequest request);

}
