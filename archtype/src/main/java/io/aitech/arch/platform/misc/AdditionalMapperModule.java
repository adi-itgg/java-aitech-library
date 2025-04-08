package io.aitech.arch.platform.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public final class AdditionalMapperModule extends SimpleModule {

  private final transient DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX").withZone(ZoneId.systemDefault());

  public AdditionalMapperModule() {
    this.addSerializer(OffsetDateTime.class, offsetDateTimeJsonSerializer());
    this.addDeserializer(OffsetDateTime.class, offsetDateTimeJsonDeserializer());
    this.addDeserializer(JsonObject.class, jsonObjectJsonDeserializer());
  }

  private JsonSerializer<OffsetDateTime> offsetDateTimeJsonSerializer() {
    return new JsonSerializer<>() {
      @Override
      public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(offsetDateTime.format(dtf));
      }
    };
  }

  private JsonDeserializer<OffsetDateTime> offsetDateTimeJsonDeserializer() {
    return new JsonDeserializer<>() {
      @Override
      public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        var text = jsonParser.getText();
        try {
          return OffsetDateTime.from(dtf.parse(text));
        } catch (DateTimeException var5) {
          throw new InvalidFormatException(jsonParser, "Expected the format should be yyyy-MM-dd'T'HH:mm:ssXXX -> 2023-12-29T23:57:50+07:00", text, OffsetDateTime.class);
        }
      }
    };
  }


  private JsonDeserializer<JsonObject> jsonObjectJsonDeserializer() {
    return new JsonDeserializer<>() {
      @SuppressWarnings("unchecked")
      @Override
      public JsonObject deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return new JsonObject(p.readValueAs(Map.class));
      }
    };
  }

}
