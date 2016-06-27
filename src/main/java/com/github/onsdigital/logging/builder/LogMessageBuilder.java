package com.github.onsdigital.logging.builder;

import ch.qos.logback.classic.Level;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by dave on 5/5/16.
 */
public abstract class LogMessageBuilder {

    protected static Logger LOG = null;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static final String ERROR_CONTENT_KEY = "errorContext";
    public static final String ERROR_CLASS_KEY = "class";
    public static final String STACK_TRACE_KEY = "stackTrace";
    public static final String MSG_KEY = "message";

    private static final ExecutorService logThreadPool = Executors.newFixedThreadPool(20);
    private static Function<ExecutorService, Thread> showDownTask = (executorService -> new Thread(() -> executorService
            .shutdown()));

    protected String description;
    protected LogParameters parameters;
    protected Level logLevel;

    static {
        Runtime.getRuntime().addShutdownHook(showDownTask.apply(logThreadPool));
    }

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
        this.parameters.getParameters().put(STACK_TRACE_KEY, ExceptionUtils.getStackTrace(t));
    }

    public LogMessageBuilder addMessage(String message) {
        this.parameters.getParameters().put(MSG_KEY, message);
        return this;
    }

    public String getLogLevel() {
        return this.logLevel.levelStr;
    }

    public DateFormat getDataFormat() {
        return DATE_FORMAT;
    }

    public LogMessageBuilder addParameter(String key, Object value) {
        this.parameters.getParameters().put(key, value);
        return this;
    }

    public void log() {
        logThreadPool.submit(() -> {
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
        });
    }

    public abstract String getLoggerName();
}
