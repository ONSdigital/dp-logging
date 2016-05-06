package com.github.onsdigital.logging.builder;

import ch.qos.logback.classic.Level;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class ComplexLogMessageBuilder extends LogMessageBuilder {

    protected Map<String, Object> parameters;

    public ComplexLogMessageBuilder(String eventDescription) {
        super(eventDescription);
        this.parameters = new HashMap<>();
    }

    public ComplexLogMessageBuilder(String eventDescription, Level logLevel) {
        super(eventDescription, logLevel);
        this.parameters = new HashMap<>();
    }

    public ComplexLogMessageBuilder addParameter(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    public ComplexLogMessageBuilder logLevel(Level level) {
        if (logLevel != null) {
            this.logLevel = level.levelStr;
        }
        return this;
    }
}
