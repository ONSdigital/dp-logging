package com.github.onsdigital.logging.v2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class HTTPAdapter implements JsonSerializer<HTTP> {

    @Override
    public JsonElement serialize(HTTP http, Type type, JsonSerializationContext context) {
        if (http == null) {
            return null;
        }

        JsonObject e = new JsonObject();
        if (isNotEmpty(http.method())) {
            e.addProperty("method", http.method());
        }

        if (isNotEmpty(http.path())) {
            e.addProperty("path", http.path());
        }

        if (e.entrySet().isEmpty()) {
            return null;
        }
        return e;
    }
}
