package com.github.onsdigital.logging.v2.serializer;

import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.event.BaseEvent;
import com.github.onsdigital.logging.v2.event.HTTP;

public interface LogSerialiser {

    <T extends BaseEvent> String marshall(T event) throws LoggingException;

    <T extends BaseEvent> String marshallWithRetry(T event) throws LoggingException;

    String marshall(HTTP http) throws LoggingException;

    HTTP getHTTP(String json) throws LoggingException;
}
