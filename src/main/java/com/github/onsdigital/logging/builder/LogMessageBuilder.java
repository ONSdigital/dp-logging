package com.github.onsdigital.logging.builder;

import ch.qos.logback.classic.Level;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by dave on 5/5/16.
 */
public abstract class LogMessageBuilder {

    protected static Logger LOG = null;
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private String description;
    private LogParameters parameters;
    private Level logLevel;

    public LogMessageBuilder(String eventDescription) {
        this(eventDescription, Level.INFO);
    }

    public LogMessageBuilder(String description, Level logLevel) {
        this.description = description;
        this.logLevel = logLevel;
        this.parameters = new LogParameters();

    }

    public LogMessageBuilder addMessage(String message) {
        this.parameters.getParameters().put("message", message);
        return this;
    }

    public String getLogLevel() {
        return this.logLevel.levelStr;
    }

    public DateFormat getDataFormat() {
        return DATE_FORMAT;
    }

    public LogMessageBuilder addParameter(String key, Object value) {
        this.parameters.getParameters().put(key, value.toString());
        return this;
    }

    public void log() {
        if (LOG == null || !StringUtils.equalsIgnoreCase(LOG.getName(), getLoggerName())) {
            LOG = getLogger(getLoggerName());
        }

        switch (Level.toLevel(getLogLevel()).levelInt) {
            case Level.ERROR_INT:
                LOG.error(this.description, this.parameters);
                break;
            case Level.WARN_INT:
                LOG.warn(this.description, this.parameters);
                break;
            case Level.INFO_INT:
                LOG.info(this.description, this.parameters);
                break;
            case Level.DEBUG_INT:
                LOG.debug(this.description, this.parameters);
                break;
            case Level.TRACE_INT:
                LOG.trace(this.description, this.parameters);
        }
    }

    public abstract String getLoggerName();
}
