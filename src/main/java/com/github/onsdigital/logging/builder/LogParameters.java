package com.github.onsdigital.logging.builder;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * POJO provides wrapper around {@link Map} class for a convenient way to pass a map of parameters as a single argument
 * to a {@link org.slf4j.Logger} log method and an easier way to identify it when processing each arg object.
 * See {@link com.github.onsdigital.logging.layouts.TextLayout#appendParameters(StringBuilder, ILoggingEvent)} for example.
 */
public class LogParameters {

    private Map<String, String> parameters;

    public LogParameters() {
        this.parameters = new LinkedHashMap<>();
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
