package com.github.onsdigital.logging.v2.storage;

import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.event.Auth;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

public class MDCLogStore implements LogStore {

    static final String HTTP_KEY = "http";
    static final String TRACE_ID_KEY = "trace_id";
    static final String REQUEST_ID_HEADER = "X-Request-Id";
    static final String MARSHALL_ERR_FMT = "failed to marshall {0}, trace_id: {1}";
    static final String UNMARSHALL_ERR_FMT = "failed to unmarshall {0}, trace_id: {1}";

    static final String AUTH_KEY = "auth";

    private LogSerialiser serialiser;

    public MDCLogStore(LogSerialiser serialiser) {
        this.serialiser = serialiser;
    }

    @Override
    public void saveTraceID(HttpServletRequest req) {
        String traceID = getTraceIDFromRequest(req);
        traceID = defaultIfBlank(traceID, newTraceID());
        MDC.put(TRACE_ID_KEY, traceID);
    }

    @Override
    public void saveTraceID(HttpUriRequest httpUriRequest) {
        Header header = httpUriRequest.getFirstHeader(REQUEST_ID_HEADER);
        String headerValue = header != null ? header.getValue() : "";
        saveTraceID(headerValue);
    }

    @Override
    public void saveTraceID(String id) {
        id = defaultIfBlank(getTraceID(), id);
        id = defaultIfBlank(id, newTraceID());
        MDC.put(TRACE_ID_KEY, id);
    }

    @Override
    public void saveAuth(Auth auth) {
        try {
            MDC.put(AUTH_KEY, serialiser.marshallAuth(auth));
        } catch (LoggingException ex) {
            LoggingException wrapper = new LoggingException(format(MARSHALL_ERR_FMT, AUTH_KEY, getTraceID()), ex);
            System.err.println(wrapper);
        }
    }

    @Override
    public String getTraceID() {
        return MDC.get(TRACE_ID_KEY);
    }

    @Override
    public Auth getAuth() {
        String authStr = MDC.get(AUTH_KEY);
        if (StringUtils.isEmpty(authStr)) {
            return null;
        }
        try {
            return serialiser.unmarshallAuth(authStr);
        } catch (LoggingException ex) {
            LoggingException wrapped = new LoggingException(format(UNMARSHALL_ERR_FMT, AUTH_KEY, getTraceID()), ex);
            System.err.println(wrapped);
            return null;
        }
    }

    private String getTraceIDFromRequest(HttpServletRequest req) {
        return defaultIfBlank(getTraceID(), req.getHeader(REQUEST_ID_HEADER));
    }

    private String newTraceID() {
        return UUID.randomUUID().toString();
    }
}
