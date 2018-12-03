package com.github.onsdigital.logging;

import ch.qos.logback.classic.Level;
import com.github.onsdigital.logging.builder.LogMessageBuilder;

import java.util.Map;

/**
 * Basic implementation for unit testing.
 */
class LogBuilder extends LogMessageBuilder {

    public LogBuilder(String eventDescription) {
        super(eventDescription);
    }

    public LogBuilder(String description, Level logLevel) {
        super(description, logLevel);
    }

    public LogBuilder(Throwable t, Level level, String description) {
        super(t, level, description);
    }

    public LogBuilder(Throwable t, String description) {
        super(t, description);
    }

    @Override
    public String getLoggerName() {
        return null;
    }

    public Map<String, Object> getParams() {
        return this.parameters.getParameters();
    }
}
