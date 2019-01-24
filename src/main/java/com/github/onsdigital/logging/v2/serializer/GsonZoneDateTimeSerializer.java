package com.github.onsdigital.logging.v2.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class GsonZoneDateTimeSerializer implements JsonSerializer<ZonedDateTime> {

    private static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private DateTimeFormatter formatter;

    public GsonZoneDateTimeSerializer() {
        this.formatter = DateTimeFormatter.ofPattern(ISO8601_FORMAT);
    }

    @Override
    public JsonElement serialize(ZonedDateTime zonedDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(formatter.format(zonedDateTime));
    }
}
