package com.github.onsdigital.logging.builder;

import ch.qos.logback.classic.Level;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by dave on 5/5/16.
 */
@Deprecated
public abstract class LogMessageBuilder {

    protected static Logger LOG = null;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static final String ERROR_CONTENT_KEY = "errorContext";
    public static final String ERROR_CLASS_KEY = "class";
    public static final String STACK_TRACE_KEY = "stackTrace";
    public static final String MSG_KEY = "message";

    protected String namespace;
    protected String description;
    protected LogParameters parameters;
    protected Level logLevel;

    public LogMessageBuilder(String eventDescription) {
        this(eventDescription, Level.INFO);
    }

    public LogMessageBuilder(String description, Level logLevel) {
        this.description = description;
        this.logLevel = logLevel;
        this.parameters = new LogParameters();
    }

    public LogMessageBuilder(Throwable t, Level level, String description) {
        this(t, description);
        this.logLevel = level;
    }

    public LogMessageBuilder(Throwable t, String description) {
        this.description = description;
        this.logLevel = Level.ERROR;
        this.parameters = new LogParameters();

        addParameter(ERROR_CONTENT_KEY, description);
        addParameter(ERROR_CLASS_KEY, t.getClass().getName());
        addParameter(STACK_TRACE_KEY, ExceptionUtils.getStackTrace(t));
    }

    public LogMessageBuilder addMessage(String message) {
        return addParameter(MSG_KEY, message);
    }

    public String getLogLevel() {
        return logLevel.levelStr;
    }

    public DateFormat getDataFormat() {
        return DATE_FORMAT;
    }

    public LogMessageBuilder addParameter(String key, Object value) {
        if (StringUtils.isBlank(key)) {
            return this;
        }

        if (value == null) {
            return this;
        }

        /**
         * Marshalling a Path object to JSON can cause a org.codehaus.jackson.map.JsonMappingException cyclic
         * reference exception. The noraml fix fo this is use the appropriate Jackson object annotation to prevent
         * this happening, however to keep the logger interface clean and hassle free we check if the value is a path
         * and use the String representation of it instead.
         */
        if (value instanceof Path) {
            value = value.toString();
        }

        parameters.getParameters().put(key, value);
        return this;
    }

    public void setNamespace(String namespace) {
        if (StringUtils.isNotEmpty(namespace)) {
            parameters.setNamespace(namespace);
        }
    }

    public void log() {
        if (LOG == null || !StringUtils.equalsIgnoreCase(LOG.getName(), getLoggerName())) {
            LOG = getLogger(getLoggerName());
        }
        switch (Level.toLevel(getLogLevel()).levelInt) {
            case Level.ERROR_INT:
                LOG.error(description, parameters);
                break;
            case Level.WARN_INT:
                LOG.warn(description, parameters);
                break;
            case Level.INFO_INT:
                LOG.info(description, parameters);
                break;
            case Level.DEBUG_INT:
                LOG.debug(description, parameters);
                break;
            case Level.TRACE_INT:
                LOG.trace(description, parameters);
        }
    }

    public abstract String getLoggerName();
}
