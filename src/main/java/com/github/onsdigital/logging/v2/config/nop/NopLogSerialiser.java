package com.github.onsdigital.logging.v2.config.nop;

import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.event.BaseEvent;
import com.github.onsdigital.logging.v2.event.HTTP;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;

public class NopLogSerialiser implements LogSerialiser {
    @Override
    public <T extends BaseEvent> String marshallEvent(T event) throws LoggingException {
        return "";
    }

    @Override
    public String marshallHTTP(HTTP http) throws LoggingException {
        return "";
    }

    @Override
    public HTTP unmarshallHTTP(String json) throws LoggingException {
        return null;
    }
}
