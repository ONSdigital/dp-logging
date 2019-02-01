package com.github.onsdigital.logging.v2.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.onsdigital.logging.v2.DPLogger;

import java.io.IOException;
import java.time.ZonedDateTime;

public class JacksonZonedlDateTimeSerialiser extends StdSerializer<ZonedDateTime> {

    public JacksonZonedlDateTimeSerialiser() {
        super(ZonedDateTime.class);
    }

    @Override
    public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(zonedDateTime.format(DPLogger.formatter()));
    }
}
