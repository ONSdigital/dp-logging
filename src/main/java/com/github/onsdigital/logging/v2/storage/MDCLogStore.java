package com.github.onsdigital.logging.v2.storage;

import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.event.HTTP;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static com.github.onsdigital.logging.v2.DPLogger.logConfig;

public class MDCLogStore implements LogStore {

    private static final String HTTP_KEY = "http";
    private static final String TRACE_ID_KEY = "trace_id";

    @Override
    public void saveHTTP(HTTP http) {
        try {
            String json = logConfig().getSerialiser().marshallHTTP(http);
            MDC.put(HTTP_KEY, json);
        } catch (LoggingException ex) {
            // TODO how to handle this.
        }
    }

    @Override
    public void saveTraceID(HttpServletRequest req) {
        String traceID = req.getHeader(TRACE_ID_KEY);
        if (StringUtils.isEmpty(traceID)) {
            traceID = UUID.randomUUID().toString();
        }
        MDC.put(TRACE_ID_KEY, traceID);
    }

    @Override
    public HTTP getHTTP() {
        String httpJson = MDC.get(HTTP_KEY);
        if (StringUtils.isEmpty(httpJson)) {
            return new HTTP();
        }
        try {
            return logConfig().getSerialiser().unmarshallHTTP(httpJson);
        } catch (LoggingException ex) {
            // TODO how to handle this.
        }
        return null;
    }

    @Override
    public String getTraceID() {
        return MDC.get(TRACE_ID_KEY);
    }
}
