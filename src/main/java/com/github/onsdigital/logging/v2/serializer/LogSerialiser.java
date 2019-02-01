package com.github.onsdigital.logging.v2.serializer;

import com.github.onsdigital.logging.v2.event.BaseEvent;
import com.github.onsdigital.logging.v2.event.HTTP;

public interface LogSerialiser {

    <T extends BaseEvent> String toJson(T event);

    String toJson(HTTP http);

    HTTP getHTTP(String json);
}
