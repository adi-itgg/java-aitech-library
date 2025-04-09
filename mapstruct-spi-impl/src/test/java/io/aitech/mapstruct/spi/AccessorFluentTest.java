package io.aitech.mapstruct.spi;

import io.aitech.mapstruct.spi.data.DataRecord;
import io.aitech.mapstruct.spi.data.FluentData;
import io.aitech.mapstruct.spi.mapper.FluentMapper;
import org.junit.jupiter.api.Test;

public class AccessorFluentTest {

  /*@Test
  public void fluentClassToRecord() {
    FluentData fluentData = new FluentData().name("test");
    DataRecord dataRecord = FluentMapper.INSTANCE.toDataRecord(fluentData);

    assert dataRecord.name().equals("test");
  }

  @Test
  public void recordToFluentClass() {
    DataRecord dataRecord = new DataRecord("test");
    FluentData fluentData = FluentMapper.INSTANCE.toFluentData(dataRecord);

    assert fluentData.name().equals("test");
  }*/

}
