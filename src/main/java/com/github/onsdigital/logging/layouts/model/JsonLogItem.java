package com.github.onsdigital.logging.layouts.model;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.onsdigital.logging.builder.LogParameters;
import org.slf4j.MDC;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.github.onsdigital.logging.util.RequestLogUtil.REMOTE_HOST_KEY;
import static com.github.onsdigital.logging.util.RequestLogUtil.REQUEST_ID_KEY;

/**
 * POJO representing a json log item.
 */
@Deprecated
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonPropertyOrder({"time", "level", "name", "thread", "request", "host"})
public class JsonLogItem {

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @JsonProperty("namespace")
    protected String namespace;

    @JsonProperty("time")
    protected String timestamp;

    @JsonProperty("level")
    protected String level;

    @JsonProperty("name")
    protected String loggerName;

    @JsonProperty("thread")
    protected String threadName;

    @JsonProperty("request")
    protected String requestId;

    @JsonProperty("host")
    protected String remoteHost;
    protected String description;
    protected Map<String, Object> parameters;
    protected transient ILoggingEvent event;

    public JsonLogItem(ILoggingEvent event) {
        this.event = event;
        this.timestamp = DATE_FORMAT.format(new Date());
        this.level = event.getLevel().levelStr;
        this.loggerName = event.getLoggerName();
        this.threadName = event.getThreadName();
        this.description = event.getFormattedMessage();
        this.requestId = MDC.get(REQUEST_ID_KEY);
        this.remoteHost = MDC.get(REMOTE_HOST_KEY);
        this.parameters = null;

        if (event.getArgumentArray().length > 0) {
            for (Object arg : event.getArgumentArray()) {
                if (arg instanceof LogParameters) {
                    LogParameters lp = (LogParameters) arg;
                    this.namespace = lp.getNamespace();
                    this.parameters = lp.getParameters();
                    break;
                }
            }
        }
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getLevel() {
        return level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
