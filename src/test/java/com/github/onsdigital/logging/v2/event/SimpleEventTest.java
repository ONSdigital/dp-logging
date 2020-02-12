package com.github.onsdigital.logging.v2.event;

import com.github.onsdigital.logging.v2.serializer.JacksonLogSerialiser;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import com.github.onsdigital.logging.v2.storage.LogStore;
import com.github.onsdigital.logging.v2.storage.MDCLogStore;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimpleEventTest {

    static final String HOST = "localhost";
    static final String SCHEME = "http";
    static final String REQUESTED_URI = "/a/b/c";
    static final String HTTP_METHOD = "GET";
    static final int PORT = 8080;
    static final String TRACE_ID = "trace_id";
    static final String REQ_ID = "X-Request-Id";
    static final String TRACE_ID_VALUE = "666";

    private LogStore logStore;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        LogSerialiser serialiser = new JacksonLogSerialiser();
        this.logStore = new MDCLogStore(serialiser);
    }

    @Test
    public void beginHTTP_HttpServletRequest_NoIDHeader_ShouldGenerateNewIDAndStoreRequestDetails() {
        HttpServletRequest req = setUpHttpServletRequest("");

        SimpleEvent event = new SimpleEvent("test", Severity.INFO, logStore, "");
        event.beginHTTP(req);

        assertFalse(StringUtils.isEmpty(event.getTraceID()));
        assertHTTPRequestDetails(event);
    }

    @Test
    public void beginHTTP_HttpServletRequest_WithIDHeader_ShouldStoreIDAndRequestDetails() {
        String expectedTraceID = "666";
        HttpServletRequest req = setUpHttpServletRequest(expectedTraceID);

        SimpleEvent event = new SimpleEvent("test", Severity.INFO, logStore, "");
        event.beginHTTP(req);

        assertThat(event.getTraceID(), equalTo(expectedTraceID));
        assertThat(MDC.get(TRACE_ID), equalTo(expectedTraceID));
        assertHTTPRequestDetails(event);
    }

    @Test
    public void
    endHTTP_HttpServletRequestAndResponse_NoIDHeader_ShouldGenerateNewIDAndStoreDetails() {
        HttpServletRequest req = setUpHttpServletRequest("");
        HttpServletResponse resp = setUpHttpServletResponse();

        SimpleEvent event = new SimpleEvent("test", Severity.INFO, logStore, "");
        event.endHTTP(req, resp);

        assertFalse(StringUtils.isEmpty(event.getTraceID()));
        assertHTTPRequestDetails(event);
        assertHTTPResponseDetails(event);
    }

    @Test
    public void beginHTTP_HttpUriRequest_NoIDHeader_ShouldGenerateNewIDAndStoreRequestDetails() {
        HttpUriRequest req = setUpHttpUriRequest("");

        SimpleEvent event = new SimpleEvent("test", Severity.INFO, logStore, "");
        event.beginHTTP(req);

        assertFalse(StringUtils.isEmpty(event.getTraceID()));
        assertHTTPRequestDetails(event);
    }

    @Test
    public void beginHTTP_HttpUriRequest_IDHeader_ShouldStoreIDAndRequestDetails() {
        HttpUriRequest req = setUpHttpUriRequest(TRACE_ID_VALUE);

        SimpleEvent event = new SimpleEvent("test", Severity.INFO, logStore, "");
        event.beginHTTP(req);

        assertThat(event.getTraceID(), equalTo(TRACE_ID_VALUE));
        assertHTTPRequestDetails(event);
    }

    @Test
    public void endHTTP_HttpUriRequestAndResponse_NoIDHeader_ShouldGenerateNewIDAndStoreDetails() {
        HttpUriRequest req = setUpHttpUriRequest("");
        HttpResponse resp = setUpHttpResponse();

        SimpleEvent event = new SimpleEvent("test", Severity.INFO, logStore, "");
        event.endHTTP(req, resp);

        assertFalse(StringUtils.isEmpty(event.getTraceID()));
        assertHTTPRequestDetails(event);
        assertHTTPResponseDetails(event);
    }

    @Test
    public void endHTTP_HttpUriRequestAndResponse_IDHeader_ShouldStoreIDAndDetails() {
        HttpServletRequest req = setUpHttpServletRequest(TRACE_ID_VALUE);
        HttpServletResponse resp = setUpHttpServletResponse();

        SimpleEvent event = new SimpleEvent("test", Severity.INFO, logStore, "");
        event.endHTTP(req, resp);

        assertThat(event.getTraceID(), equalTo(TRACE_ID_VALUE));
        assertHTTPRequestDetails(event);
        assertHTTPResponseDetails(event);
    }

    void assertHTTPRequestDetails(SimpleEvent actual) {
        assertThat(actual.getHttp().getMethod(), equalTo(HTTP_METHOD));
        assertThat(actual.getHttp().getPath(), equalTo(REQUESTED_URI));
        assertThat(actual.getHttp().getQuery(), equalTo(""));
        assertThat(actual.getHttp().getScheme(), equalTo(SCHEME));
        assertThat(actual.getHttp().getHost(), equalTo(HOST));
        assertThat(actual.getHttp().getPort(), equalTo(PORT));
    }

    void assertHTTPResponseDetails(SimpleEvent actual) {
        assertThat(actual.getHttp().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    HttpServletRequest setUpHttpServletRequest(String requestID) {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader(REQ_ID)).thenReturn(requestID);

        when(request.getMethod()).thenReturn(HTTP_METHOD);

        when(request.getRequestURI()).thenReturn(REQUESTED_URI);

        when(request.getQueryString()).thenReturn("");

        when(request.getScheme()).thenReturn(SCHEME);

        when(request.getServerName()).thenReturn(HOST);

        when(request.getServerPort()).thenReturn(PORT);

        return request;
    }

    HttpServletResponse setUpHttpServletResponse() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(response.getStatus()).thenReturn(HttpStatus.SC_OK);

        return response;
    }

    HttpUriRequest setUpHttpUriRequest(String traceID) {
        HttpUriRequest req = mock(HttpUriRequest.class);

        when(req.getMethod()).thenReturn(HTTP_METHOD);

        URI uri = URI.create("http://localhost:8080/a/b/c");
        when(req.getURI()).thenReturn(uri);

        if (StringUtils.isNotEmpty(traceID)) {
            Header h = mock(Header.class);

            when(h.getValue()).thenReturn(traceID);

            when(req.getFirstHeader(REQ_ID)).thenReturn(h);
        }

        return req;
    }

    HttpResponse setUpHttpResponse() {
        HttpResponse resp = mock(HttpResponse.class);

        StatusLine statusLine = mock(StatusLine.class);
        when(resp.getStatusLine()).thenReturn(statusLine);

        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        return resp;
    }
}
