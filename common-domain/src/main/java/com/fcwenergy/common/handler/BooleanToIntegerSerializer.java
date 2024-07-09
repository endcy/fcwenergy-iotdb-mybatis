package com.fcwenergy.common.handler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Objects;

public class BooleanToIntegerSerializer extends JsonSerializer<Boolean> {


    @Override
    public void serialize(Boolean aBoolean, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(Objects.isNull(aBoolean) || Boolean.FALSE.equals(aBoolean) ? 0 : 1);
    }
}
