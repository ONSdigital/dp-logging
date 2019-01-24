package com.github.onsdigital.logging.v2.serializer;

import com.github.onsdigital.logging.v2.event.BaseEvent;

@FunctionalInterface
public interface EventSerializer {

    <T extends BaseEvent> String toJson(T event);
}
