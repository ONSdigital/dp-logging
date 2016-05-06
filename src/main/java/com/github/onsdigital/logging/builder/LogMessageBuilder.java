package com.github.onsdigital.logging.builder;

import ch.qos.logback.classic.Level;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.github.onsdigital.logging.util.RequestLogUtil.REMOTE_HOST;
import static com.github.onsdigital.logging.util.RequestLogUtil.REQUEST_ID_KEY;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by dave on 5/5/16.
 */
public abstract class LogMessageBuilder {

    protected static Logger LOG = null;
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    protected String eventDescription;
    protected String requestId;
    protected String remoteHost;
    protected String timestamp;
    protected String logLevel;

    public LogMessageBuilder(String eventDescription) {
        this.logLevel = Level.INFO.levelStr;
        this.eventDescription = eventDescription;
        this.timestamp = DATE_FORMAT.format(new Date());
        this.requestId = MDC.get(REQUEST_ID_KEY);
        this.remoteHost = MDC.get(REMOTE_HOST);
        this.logLevel = Level.INFO.levelStr;
    }

    public LogMessageBuilder(String eventDescription, Level logLevel) {
        this.logLevel = logLevel.levelStr;
        this.eventDescription = eventDescription;
        this.timestamp = DATE_FORMAT.format(new Date());
        this.requestId = MDC.get(REQUEST_ID_KEY);
        this.remoteHost = MDC.get(REMOTE_HOST);
        this.logLevel = logLevel.levelStr;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String getLogLevel() {
        return logLevel;
    }

    public DateFormat getDataFormat() {
        return DATE_FORMAT;
    }

    public void log() {
        if (LOG == null || !StringUtils.equalsIgnoreCase(LOG.getName(), getLoggerName())) {
            LOG = getLogger(getLoggerName());
        }

        switch (Level.toLevel(logLevel).levelInt) {
            case Level.ERROR_INT:
                LOG.error(toJson());
                break;
            case Level.WARN_INT:
                LOG.warn(toJson());
                break;
            case Level.INFO_INT:
                LOG.info(toJson());
                break;
            case Level.DEBUG_INT:
                LOG.debug(toJson());
                break;
            case Level.TRACE_INT:
                LOG.trace(toJson());
        }
    }

    public abstract String getLoggerName();
}
