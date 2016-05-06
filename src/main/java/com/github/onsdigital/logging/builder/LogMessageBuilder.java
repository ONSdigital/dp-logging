package com.github.onsdigital.logging.builder;

import ch.qos.logback.classic.Level;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.github.onsdigital.logging.util.RequestLogUtil.REMOTE_HOST;
import static com.github.onsdigital.logging.util.RequestLogUtil.REQUEST_ID_KEY;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by dave on 5/5/16.
 */
public abstract class LogMessageBuilder {

    protected static Logger LOG = null;
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    protected Map<String, Object> jsonMap = new HashMap<>();
    protected Map<String, Object> parameters = new HashMap<>();

    public LogMessageBuilder(String eventDescription) {
        jsonMap.put("logLevel", Level.INFO.levelStr);
        jsonMap.put("eventDescription", eventDescription);
        jsonMap.put("timestamp", DATE_FORMAT.format(new Date()));
        jsonMap.put("requestId", MDC.get(REQUEST_ID_KEY));
        jsonMap.put("remoteHost", MDC.get(REMOTE_HOST));
        jsonMap.put("parameters", parameters);
    }

    public LogMessageBuilder(String eventDescription, Level logLevel) {
        jsonMap.put("logLevel", logLevel.levelStr);
        jsonMap.put("eventDescription", eventDescription);
        jsonMap.put("timestamp", DATE_FORMAT.format(new Date()));
        jsonMap.put("requestId", MDC.get(REQUEST_ID_KEY));
        jsonMap.put("remoteHost", MDC.get(REMOTE_HOST));

    }

    public String toJson() {
        jsonMap.put("parameters", parameters);
        return new Gson().toJson(jsonMap);
    }

    public String getLogLevel() {
        return (String) jsonMap.get("logLevel");
    }

    public DateFormat getDataFormat() {
        return DATE_FORMAT;
    }

    public LogMessageBuilder addParameter(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    public void log() {
        if (LOG == null || !StringUtils.equalsIgnoreCase(LOG.getName(), getLoggerName())) {
            LOG = getLogger(getLoggerName());
        }

        switch (Level.toLevel(getLogLevel()).levelInt) {
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
