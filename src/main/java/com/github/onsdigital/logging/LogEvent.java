package com.github.onsdigital.logging;

import com.github.onsdigital.logging.util.LoggingThread;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dave on 5/4/16.
 */
public class LogEvent {

    private static final Logger LOG = LoggerFactory.getLogger("com.github.onsdigital.logging");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    protected String eventDescription;
    protected String requestId;
    protected String remoteHost;
    protected String timestamp;
    protected Map<String, Object> parameters;

    public LogEvent(String eventDescription) {
        this.eventDescription = eventDescription;
        this.parameters = new HashMap<>();
    }

    public LogEvent addParameter(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    public void logIt() {
        this.requestId = LoggingThread.get(LoggingThread.REQUEST_ID_KEY);
        this.remoteHost = LoggingThread.get(LoggingThread.REMOTE_HOST);
        this.timestamp = DATE_FORMAT.format(new Date());
        LOG.info(new Gson().toJson(this));
    }
}
