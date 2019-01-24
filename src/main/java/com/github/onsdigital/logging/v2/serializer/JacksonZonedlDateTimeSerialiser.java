package com.github.onsdigital.logging.v2.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class JacksonZonedlDateTimeSerialiser extends StdSerializer<ZonedDateTime> {

    private static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private DateTimeFormatter formatter;

    public JacksonZonedlDateTimeSerialiser() {
        super(ZonedDateTime.class);
        formatter = DateTimeFormatter.ofPattern(ISO8601_FORMAT, Locale.UK);
    }

    @Override
    public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(zonedDateTime.format(formatter));
    }
}
