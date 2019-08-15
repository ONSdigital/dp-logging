package com.github.onsdigital.logging.builder;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * POJO provides wrapper around {@link Map} class for a convenient way to pass a map of parameters as a single argument
 * to a {@link org.slf4j.Logger} log method and an easier way to identify it when processing each arg object.
 * See {@link com.github.onsdigital.logging.layouts.TextLayout#appendParameters(StringBuilder, ILoggingEvent)} for example.
 */
@Deprecated
public class LogParameters {

    private Map<String, Object> parameters;
    private String namespace;

    public LogParameters() {
        this.parameters = new LinkedHashMap<>();
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return this.namespace;
    }
}
