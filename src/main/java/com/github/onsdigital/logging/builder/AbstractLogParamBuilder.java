package com.github.onsdigital.logging.builder;

import com.github.onsdigital.logging.util.LoggingThread;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines base implementation for {@link LogParamBuilder}. {@link LogParamBuilder} is used to construct log messages,
 * Invoke {@link AbstractLogParamBuilder#build()} to create an {@link EventLogger} to log your events. Extend this class
 * to customise LogParamBuilder to fit your specific requirements.
 */
public abstract class AbstractLogParamBuilder implements LogParamBuilder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    protected String eventDescription;
    protected String requestId;
    protected String remoteHost;
    protected String timestamp;
    protected Map<String, Object> parameters;

    public AbstractLogParamBuilder(String eventDescription) {
        this.eventDescription = eventDescription;
        this.parameters = new HashMap<>();
        this.timestamp = DATE_FORMAT.format(new Date());
        this.requestId = LoggingThread.get(LoggingThread.REQUEST_ID_KEY);
        this.remoteHost = LoggingThread.get(LoggingThread.REMOTE_HOST);
    }

    public AbstractLogParamBuilder addParameter(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public EventLogger build() {
        return new EventLogger(getLoggerName(), toJson());
    }

    public abstract String getLoggerName();

    public static class EventLogger {
        private static Logger EVENT_LOGGER = null;

        private String jsonMessage;

        private EventLogger(String logName, String message) {
            if (EVENT_LOGGER == null || !StringUtils.equalsIgnoreCase(EVENT_LOGGER.getName(), logName)) {
                EVENT_LOGGER = LoggerFactory.getLogger(logName);
            }
            this.jsonMessage = message;
        }

        public void log() {
            EVENT_LOGGER.info(jsonMessage);
        }
    }
}
