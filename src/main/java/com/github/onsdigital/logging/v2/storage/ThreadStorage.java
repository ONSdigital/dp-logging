package com.github.onsdigital.logging.v2.storage;

import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.event.HTTP;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static com.github.onsdigital.logging.v2.DPLogger.logConfig;

public class ThreadStorage {

    private static final String HTTP_KEY = "http";
    private static final String TRACE_ID_KEY = "trace_id";

    private ThreadStorage() {
        // only static methods so hide constructor.
    }

    public static void storeHTTP(HTTP http) {
        try {
            String json = logConfig().getSerialiser().marshall(http);
            MDC.put(HTTP_KEY, logConfig().getSerialiser().marshall(http));
        } catch (LoggingException ex) {
            // TODO how to handle this.
        }
    }

    public static HTTP retrieveHTTP() {
        String httpJson = MDC.get(HTTP_KEY);
        if (StringUtils.isEmpty(httpJson)) {
            return new HTTP();
        }
        try {
            logConfig().getSerialiser().getHTTP(httpJson);
        } catch (LoggingException ex) {
            // TODO how to handle this.
        }
        return null;
    }

    public static void storeTraceID(HttpServletRequest req) {
        String traceID = req.getHeader(TRACE_ID_KEY);
        if (StringUtils.isEmpty(traceID)) {
            traceID = UUID.randomUUID().toString();
        }
        MDC.put(TRACE_ID_KEY, traceID);
    }

    public static String retrieveTraceID() {
        return MDC.get(TRACE_ID_KEY);
    }
}
