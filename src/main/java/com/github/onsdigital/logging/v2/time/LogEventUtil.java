package com.github.onsdigital.logging.v2.time;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class LogEventUtil {

    /**
     * 'http request started' at key for MDC map
     **/
    private static final String HTTP_STARTED_AT_KEY = "http_started_at";

    private static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(ISO8601_FORMAT);

    private LogEventUtil() {
        // only static methods so hide constructor.
    }

    /**
     * @return {@link DateTimeFormatter} for ISO8601 format used to format all dates in the logger.
     */
    public static DateTimeFormatter formatter() {
        return FORMATTER;
    }

    /**
     * Add the 'http request started at' {@link ZonedDateTime} to the {@link MDC} map.
     *
     * @param startedAt the value to add.
     * @return return the same value.
     */
    public static ZonedDateTime setHTTPStartedAt(ZonedDateTime startedAt) {
        MDC.put(HTTP_STARTED_AT_KEY, FORMATTER.format(startedAt));
        return startedAt;
    }

    public static void setTraceID(HttpServletRequest req) {
        String traceID = req.getHeader("trace_id");
        if (StringUtils.isEmpty(traceID)) {
            traceID = UUID.randomUUID().toString();
        }
        MDC.put("trace_id", traceID);
    }

    public static String getTraceID() {
        return MDC.get("trace_id");
    }

    /**
     * Add the 'http request started at' {@link ZonedDateTime} (using the current time) to the {@link MDC} map.
     */
    public static void setHTTPStartedAt() {
        MDC.put(HTTP_STARTED_AT_KEY, FORMATTER.format(ZonedDateTime.now()));
    }

    /**
     * @return a {@link ZonedDateTime} value from the {@link MDC} map if it exists, if not return the current time.
     */
    public static ZonedDateTime getHTTPStartedAt() {
        String val = MDC.get(HTTP_STARTED_AT_KEY);
        if (StringUtils.isEmpty(val)) {
            return ZonedDateTime.now();
        }
        return ZonedDateTime.parse(val);
    }
}
