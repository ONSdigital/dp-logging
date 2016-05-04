package com.github.onsdigital.logging;

import com.github.onsdigital.logging.util.ThreadContextUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dave on 5/3/16.
 */
public class LogBuilder {

    private static final Logger LOG = LoggerFactory.getLogger("com.github.onsdigital.logging");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private String event;
    private String timestamp;
    private String requestId;
    private String remoteHost;
    private Map<String, Object> data;

    LogBuilder(Loggable event) {
        this.event = event.getName();
    }

    public LogBuilder addData(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, value);
        return this;
    }

    public void logIt() {
        this.requestId = ThreadContextUtil.get(ThreadContextUtil.REQUEST_ID_KEY);
        this.remoteHost = ThreadContextUtil.get(ThreadContextUtil.REMOTE_HOST);
        this.timestamp = DATE_FORMAT.format(new Date());
        LOG.info(new Gson().toJson(this));
    }
}
