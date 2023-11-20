package com.github.onsdigital.logging.util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;

import java.util.UUID;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.TraceId;

/**
 * Utility class providing common functionality for extracting details required for logging from in coming requests and
 * setting request logging headers for outbound request.
 */
public class RequestLogUtil {

    public static final String REQUEST_ID_KEY = "X-Request-Id";
    public static final String REMOTE_HOST_KEY = "host";
    private static final String DEFAULT_REMOTE_IP = "localhost";

    private RequestLogUtil() {
        // Util class hide the constructor.
    }

    public static void extractDiagnosticContext(HttpServletRequest request) {

        if (requestID == null) {
            requestID = UUID.randomUUID().toString();
        }

        try {
            String otelTraceID = TraceId.fromBytes(Span.current().getSpanContext().getTraceIdBytes());
            requestID = TraceId.isValid(otelTraceID) ? otelTraceID : requestID;
        } catch (Throwable e) {
            e.printStackTrace(System.err);            
        }

        MDC.put(REQUEST_ID_KEY, requestID);
        MDC.put(REMOTE_HOST_KEY, request.getHeader(REMOTE_HOST_KEY));
    }

    public static NameValuePair[] getLogHeaders() {
        NameValuePair[] headers = new NameValuePair[2];
        headers[0] = new BasicNameValuePair(REQUEST_ID_KEY, MDC.get(REQUEST_ID_KEY));
        headers[1] = new BasicNameValuePair(REMOTE_HOST_KEY, MDC.get(REMOTE_HOST_KEY));
        return headers;
    }

    private static String getHostname(HttpServletRequest request) {
        String remoteIP = DEFAULT_REMOTE_IP;

        if (request != null) {
            remoteIP = request.getHeader("X-Forwarded-For");
            if (remoteIP == null || remoteIP.length() == 0) {
                remoteIP = request.getRemoteAddr() != null ? request.getRemoteAddr() : DEFAULT_REMOTE_IP;
            }
        }
        return remoteIP;
    }
}
