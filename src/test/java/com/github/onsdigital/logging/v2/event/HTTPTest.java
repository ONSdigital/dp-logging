package com.github.onsdigital.logging.v2.event;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class HTTPTest {

    static final String HOST = "localhost";
    static final String SCHEME = "http";
    static final String REQUESTED_URI = "/a/b/c";
    static final String HTTP_METHOD = "GET";
    static final int PORT = 8080;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private HttpUriRequest httpUriRequest;

    @Mock
    private HttpResponse httpResponse;

    @Mock
    private StatusLine statusLine;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void newHTTP_HttpServletRequestNull_shouldHaveNullFields() {
        HttpServletRequest request = null;

        HTTP actual = new HTTP(request);

        assertThat(actual.getStatusCode(), is(nullValue()));
        assertThat(actual.getMethod(), is(nullValue()));
        assertThat(actual.getPath(), is(nullValue()));
        assertThat(actual.getQuery(), is(nullValue()));
        assertThat(actual.getScheme(), is(nullValue()));
        assertThat(actual.getPort(), is(nullValue()));
        assertThat(actual.getDuration(), is(nullValue()));
    }

    @Test
    public void newHTTP_ValidHttpServletRequest_shouldPopulateFields() {
        setUpHttpServletRequest();

        HTTP actual = new HTTP(httpServletRequest);

        assertThat(actual.getStatusCode(), is(nullValue()));
        assertThat(actual.getDuration(), is(nullValue()));
        assertThat(actual.getMethod(), equalTo(HTTP_METHOD));
        assertThat(actual.getPath(), equalTo(REQUESTED_URI));
        assertThat(actual.getQuery(), equalTo(""));
        assertThat(actual.getScheme(), equalTo(SCHEME));
        assertThat(actual.getHost(), equalTo(HOST));
        assertThat(actual.getPort(), equalTo(PORT));
    }

    @Test
    public void newHTTP_ValidHttpServletRequestResponseNull_shouldPopulateFields() {
        setUpHttpServletRequest();

        HTTP actual = new HTTP(httpServletRequest, null);

        assertThat(actual.getStatusCode(), is(nullValue()));
        assertThat(actual.getDuration(), is(nullValue()));
        assertThat(actual.getMethod(), equalTo(HTTP_METHOD));
        assertThat(actual.getPath(), equalTo(REQUESTED_URI));
        assertThat(actual.getQuery(), equalTo(""));
        assertThat(actual.getScheme(), equalTo(SCHEME));
        assertThat(actual.getHost(), equalTo(HOST));
        assertThat(actual.getPort(), equalTo(PORT));
    }

    @Test
    public void newHTTP_ValidHttpServletRequestResponse_shouldPopulateFields() {
        setUpHttpServletRequest();

        setUpHttpServletResponse();

        HTTP actual = new HTTP(httpServletRequest, httpServletResponse);

        assertThat(actual.getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertThat(actual.getDuration(), is(nullValue()));
        assertThat(actual.getMethod(), equalTo(HTTP_METHOD));
        assertThat(actual.getPath(), equalTo(REQUESTED_URI));
        assertThat(actual.getQuery(), equalTo(""));
        assertThat(actual.getScheme(), equalTo(SCHEME));
        assertThat(actual.getHost(), equalTo(HOST));
        assertThat(actual.getPort(), equalTo(PORT));
    }

    @Test
    public void newHTTP_nullHttpUriRequest_fieldsShouldBeNull() {
        HttpUriRequest req = null;

        HTTP actual = new HTTP(req);

        assertThat(actual.getStatusCode(), is(nullValue()));
        assertThat(actual.getMethod(), is(nullValue()));
        assertThat(actual.getPath(), is(nullValue()));
        assertThat(actual.getQuery(), is(nullValue()));
        assertThat(actual.getScheme(), is(nullValue()));
        assertThat(actual.getPort(), is(nullValue()));
        assertThat(actual.getDuration(), is(nullValue()));
    }

    @Test
    public void newHTTP_ValidHttpUriRequest_shouldPopulateFields() {
        setUpHttpUriRequest();

        HTTP actual = new HTTP(httpUriRequest);

        assertThat(actual.getStatusCode(), is(nullValue()));
        assertThat(actual.getDuration(), is(nullValue()));
        assertThat(actual.getMethod(), equalTo(HTTP_METHOD));
        assertThat(actual.getPath(), equalTo(REQUESTED_URI));
        assertThat(actual.getQuery(), equalTo(""));
        assertThat(actual.getScheme(), equalTo(SCHEME));
        assertThat(actual.getHost(), equalTo(HOST));
        assertThat(actual.getPort(), equalTo(PORT));
    }

    @Test
    public void newHTTP_ValidHttpUriRequestNullResponse_shouldPopulateRequestFields() {
        setUpHttpUriRequest();

        HTTP actual = new HTTP(httpUriRequest, null);

        assertThat(actual.getStatusCode(), is(nullValue()));
        assertThat(actual.getDuration(), is(nullValue()));
        assertThat(actual.getMethod(), equalTo(HTTP_METHOD));
        assertThat(actual.getPath(), equalTo(REQUESTED_URI));
        assertThat(actual.getQuery(), equalTo(""));
        assertThat(actual.getScheme(), equalTo(SCHEME));
        assertThat(actual.getHost(), equalTo(HOST));
        assertThat(actual.getPort(), equalTo(PORT));
    }

    @Test
    public void newHTTP_ValidHttpUriRequestAndHttpResponseNullResponse_shouldPopulateFields() {
        setUpHttpUriRequest();

        setUpHttpResponse();

        HTTP actual = new HTTP(httpUriRequest, httpResponse);

        assertThat(actual.getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertThat(actual.getDuration(), is(nullValue()));
        assertThat(actual.getMethod(), equalTo(HTTP_METHOD));
        assertThat(actual.getPath(), equalTo(REQUESTED_URI));
        assertThat(actual.getQuery(), equalTo(""));
        assertThat(actual.getScheme(), equalTo(SCHEME));
        assertThat(actual.getHost(), equalTo(HOST));
        assertThat(actual.getPort(), equalTo(PORT));
    }

    void setUpHttpServletRequest() {
        when(httpServletRequest.getMethod())
                .thenReturn(HTTP_METHOD);

        when(httpServletRequest.getRequestURI()).thenReturn(REQUESTED_URI);

        when(httpServletRequest.getQueryString())
                .thenReturn("");

        when(httpServletRequest.getScheme())
                .thenReturn(SCHEME);

        when(httpServletRequest.getServerName())
                .thenReturn(HOST);

        when(httpServletRequest.getServerPort())
                .thenReturn(PORT);
    }

    void setUpHttpServletResponse() {
        when(httpServletResponse.getStatus()).thenReturn(HttpStatus.SC_OK);
    }

    void setUpHttpUriRequest() {
        URI uri = URI.create("http://localhost:8080/a/b/c");

        when(httpUriRequest.getMethod())
                .thenReturn(HTTP_METHOD);

        when(httpUriRequest.getURI())
                .thenReturn(uri);
    }

    void setUpHttpResponse() {
        when(httpResponse.getStatusLine())
                .thenReturn(statusLine);

        when(statusLine.getStatusCode())
                .thenReturn(HttpStatus.SC_OK);
    }
}
