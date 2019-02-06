package com.github.onsdigital.logging.v2.serializer;

import com.github.onsdigital.logging.v2.event.BaseEvent;

@FunctionalInterface
public interface ErrorEventMapper<T extends BaseEvent> {

    T map(Exception ex, String message, T originalEvent);
}
