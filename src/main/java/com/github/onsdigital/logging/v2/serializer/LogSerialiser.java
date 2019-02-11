package com.github.onsdigital.logging.v2.serializer;

import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.event.Auth;
import com.github.onsdigital.logging.v2.event.BaseEvent;
import com.github.onsdigital.logging.v2.event.HTTP;

public interface LogSerialiser {

    /**
     * Marshall {@link <T extends BaseEvent>} to a json string
     */
    <T extends BaseEvent> String marshallEvent(T event) throws LoggingException;

    /**
     * Marshall an {@link <T extends BaseEvent>} to a json string.
     */
    String marshallHTTP(HTTP http) throws LoggingException;

    /**
     * Unmarshall a json string into a {@link HTTP}
     */
    HTTP unmarshallHTTP(String json) throws LoggingException;

    String marshallAuth(Auth auth) throws LoggingException;
}
