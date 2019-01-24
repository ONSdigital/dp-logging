package com.github.onsdigital.logging.v2.serializer;

import com.github.onsdigital.logging.v2.event.BaseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.ZonedDateTime;

public class GsonEventSerializer implements EventSerializer {

    static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private Gson gson;

    public GsonEventSerializer() {
        gson = new GsonBuilder()
                .setDateFormat(ISO8601_DATE_FORMAT)
                .registerTypeAdapter(ZonedDateTime.class, new GsonZoneDateTimeSerializer())
                .setPrettyPrinting()
                .create();
    }

    @Override
    public <T extends BaseEvent> String toJson(T event) {
        return gson.toJson(event);
    }
}
