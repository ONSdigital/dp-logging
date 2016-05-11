package com.github.onsdigital.logging.layouts;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.onsdigital.logging.builder.LogParameters;
import com.google.gson.Gson;
import org.slf4j.MDC;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.github.onsdigital.logging.util.RequestLogUtil.REMOTE_HOST_KEY;
import static com.github.onsdigital.logging.util.RequestLogUtil.REQUEST_ID_KEY;

/**
 * POJO representing a json log item.
 */
public class JsonLogItem {

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private String timestamp;
    private String level;
    private String loggerName;
    private String threadName;
    private String requestId;
    private String remoteHost;
    private String description;
    private Map<String, String> parameters;

    public JsonLogItem(ILoggingEvent event) {
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
                    this.parameters = ((LogParameters) arg).getParameters();
                    break;
                }
            }
        }
    }

    public String asJson() {
        return new Gson().toJson(this);
    }
}
