package com.github.onsdigital.logging.v2.event;

import com.github.onsdigital.logging.v2.storage.LogStore;

import static com.github.onsdigital.logging.v2.DPLogger.logConfig;

public class SimpleEvent extends BaseEvent<SimpleEvent> {

    public SimpleEvent(String namespace, Severity severity) {
        super(namespace, severity, logConfig().getLogStore());
    }

    public SimpleEvent(String namespace, Severity severity, String event) {
        super(namespace, severity, logConfig().getLogStore(), event);
    }

    public SimpleEvent(String namespace, Severity severity, LogStore logStore, String event) {
        super(namespace, severity, logStore, event);
    }

    public static SimpleEvent debug() {
        return new SimpleEvent(logConfig().getNamespace(), Severity.DEBUG);
    }

    public static SimpleEvent info() {
        return new SimpleEvent(logConfig().getNamespace(), Severity.INFO);
    }

    public static SimpleEvent error() {
        return new SimpleEvent(logConfig().getNamespace(), Severity.ERROR);
    }

    public static SimpleEvent error(String event) {
        return new SimpleEvent(logConfig().getNamespace(), Severity.ERROR, event);
    }

    public static SimpleEvent fatal(Throwable t) {
        return new SimpleEvent(logConfig().getNamespace(), Severity.FATAL);
    }

    public static SimpleEvent warn() {
        return new SimpleEvent(logConfig().getNamespace(), Severity.WARN);
    }
}
