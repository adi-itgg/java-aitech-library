package io.aitech.mapstruct.spi.mapper;

import io.aitech.mapstruct.spi.data.DataRecord;
import io.aitech.mapstruct.spi.data.FluentData;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FluentMapper {

  FluentMapper INSTANCE = Mappers.getMapper( FluentMapper.class );

  DataRecord toDataRecord(FluentData fluentData);

  FluentData toFluentData(DataRecord dataRecord);

}
