package com.github.onsdigital.logging.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Supplier;

/**
 * Utility class providing common functionality for extracting details required for logging from in coming requests and
 * setting request logging headers for outbound request.
 */
public class RequestLogUtil {

    public static final String REQUEST_ID_KEY = "X-Request-Id";
    public static final String REMOTE_HOST_KEY = "host";
    private static final String DEFAULT_REMOTE_IP = "localhost";
    private static final int REQUEST_ID_LENGTH = 16;

    // default implementation.
    private static Supplier<String> requestIdSupplier = () -> RandomStringUtils.randomAlphanumeric(REQUEST_ID_LENGTH);

    static void setRequestIdSupplier(Supplier<String> supplier) {
        requestIdSupplier = supplier;
    }

    private static String extendRequestID(String requestID) {

        int subStringLength = REQUEST_ID_LENGTH / 2;
        String stringToAppend = RandomStringUtils.randomAlphanumeric(subStringLength);
        requestID += ", " + stringToAppend;

        return requestID;
    }

    private RequestLogUtil() {
        // Util class hide the constructor.
    }

    public static void extractDiagnosticContext(HttpServletRequest request) {
        String requestID = request.getHeader(REQUEST_ID_KEY);

        // if no request ID, supply one
        if (StringUtils.isEmpty(requestID)) {
            requestID = requestIdSupplier.get();
        }
        else {
            // else, extend existing ID
            requestID = extendRequestID(requestID);
        }

        MDC.put(REQUEST_ID_KEY, requestID);

        String host = request.getHeader(REMOTE_HOST_KEY);
        if (StringUtils.isNotEmpty(host)) {
            MDC.put(REMOTE_HOST_KEY, host);
        }
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
