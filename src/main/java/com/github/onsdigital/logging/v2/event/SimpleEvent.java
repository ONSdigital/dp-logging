package com.github.onsdigital.logging.v2.event;

public class SimpleEvent extends BaseEvent<SimpleEvent> {

    public SimpleEvent(String namespace, Severity severity) {
        super(namespace, severity);
    }
}
