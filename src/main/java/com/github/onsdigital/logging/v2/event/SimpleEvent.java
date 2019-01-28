package com.github.onsdigital.logging.v2.event;

import static com.github.onsdigital.logging.v2.DPLogger.logConfig;

public class SimpleEvent extends BaseEvent<SimpleEvent> {

    SimpleEvent(String namespace, Severity severity) {
        super(namespace, severity);
    }

    public static SimpleEvent info() {
        return new SimpleEvent(logConfig().getNamespace(), Severity.INFO);
    }

    public static SimpleEvent error() {
        return new SimpleEvent(logConfig().getNamespace(), Severity.ERROR);
    }

    public static SimpleEvent fatal(Throwable t) {
        return new SimpleEvent(logConfig().getNamespace(), Severity.FATAL);
    }

    public static SimpleEvent warn() {
        return new SimpleEvent(logConfig().getNamespace(), Severity.WARN);
    }
}
