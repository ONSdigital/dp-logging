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

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.TraceId;

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

    private String overrideRequestIDwithOtelTraceID(String requestID) {
        String otelTraceID = TraceId.fromBytes(Span.current().getSpanContext().getTraceIdBytes());
        String id = TraceId.isValid(otelTraceID) ? otelTraceID : requestID;
        return id;
    }

    @Override
    public String saveTraceID(HttpServletRequest req) {
        String id = req == null ? "" : req.getHeader(REQUEST_ID_HEADER);
        return saveTraceID(id);
    }

    @Override
    public String saveTraceID(HttpUriRequest httpUriRequest) {
        String id = "";
        if (httpUriRequest != null) {
            Header header = httpUriRequest.getFirstHeader(REQUEST_ID_HEADER);
            id = getHeaderValue(header);
        }
        return saveTraceID(id);
    }

    @Override
    public String saveTraceID(String id) {
        id =  overrideRequestIDwithOtelTraceID(defaultIfBlank(id, newTraceID()));
        MDC.put(TRACE_ID_KEY, id);
        return id;
    }

    @Override
    public void saveAuth(Auth auth) {
        try {
            MDC.put(AUTH_KEY, serialiser.marshallAuth(auth));
        } catch (LoggingException ex) {
            LoggingException wrapper =
                    new LoggingException(format(MARSHALL_ERR_FMT, AUTH_KEY, getTraceID()), ex);
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
            LoggingException wrapped =
                    new LoggingException(format(UNMARSHALL_ERR_FMT, AUTH_KEY, getTraceID()), ex);
            System.err.println(wrapped);
            return null;
        }
    }

    private String getTraceIDForRequest(HttpServletRequest req) {
        return defaultIfBlank(req.getHeader(TRACE_ID_KEY), newTraceID());
    }

    private String newTraceID() {
        return UUID.randomUUID().toString();
    }

    private String getHeaderValue(Header header) {
        String val = "";
        if (header != null) {
            val = header.getValue();
        }
        return val;
    }
}
