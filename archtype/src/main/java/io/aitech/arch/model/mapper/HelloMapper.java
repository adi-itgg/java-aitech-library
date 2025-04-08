package io.aitech.arch.model.mapper;

import io.aitech.arch.model.dto.HelloRequest;
import io.aitech.arch.model.dto.HelloResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jsr330")
public interface HelloMapper {

  HelloResponse toResponse(HelloRequest request);

}
