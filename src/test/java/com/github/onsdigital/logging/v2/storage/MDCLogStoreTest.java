package com.github.onsdigital.logging.v2.storage;

import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.event.Auth;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintStream;
import java.util.UUID;

import static com.github.onsdigital.logging.v2.storage.MDCLogStore.AUTH_KEY;
import static com.github.onsdigital.logging.v2.storage.MDCLogStore.MARSHALL_ERR_FMT;
import static com.github.onsdigital.logging.v2.storage.MDCLogStore.REQUEST_ID_HEADER;
import static com.github.onsdigital.logging.v2.storage.MDCLogStore.TRACE_ID_KEY;
import static com.github.onsdigital.logging.v2.storage.MDCLogStore.UNMARSHALL_ERR_FMT;
import static java.text.MessageFormat.format;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MDCLogStoreTest {

    private static final String TRACE_ID = "666";

    @Mock
    private HttpServletRequest request;

    @Mock
    private LogSerialiser serialiser;

    private LogStore store;

    private String httpJson = "  \"http\" : {\n" +
            "    \"method\" : \"GET\",\n" +
            "    \"path\" : \"/hello\",\n" +
            "    \"scheme\" : \"http\",\n" +
            "    \"host\" : \"localhost\",\n" +
            "    \"port\" : 4567,\n" +
            "    \"duration\" : 24000000,\n" +
            "    \"status_code\" : 200,\n" +
            "    \"started_at\" : \"2019-02-07T12:15:13.971Z\",\n" +
            "    \"ended_at\" : \"2019-02-07T12:15:13.995Z\"\n" +
            "  }";

    private String authJson = "{\"auth\": {\"identity\": \"mr.pickles@ons.gov.uk\", \"identity_type\": \"user\"}}";

    @Before
    public void setUp() {
        store = new MDCLogStore(serialiser);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/home");
        when(request.getQueryString()).thenReturn("");
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(666);
    }

    @After
    public void tearDown() {
        MDC.clear();
    }

    @Test
    public void testGetTraceIDSuccess() {
        MDC.put(TRACE_ID_KEY, TRACE_ID);
        assertThat(store.getTraceID(), equalTo(TRACE_ID));
    }

    @Test
    public void testSaveTraceIDSuccess() {
        when(request.getHeader(REQUEST_ID_HEADER))
                .thenReturn(TRACE_ID);

        String actual = store.saveTraceID(request);

        assertThat(actual, equalTo(TRACE_ID));
    }

    @Test
    public void testSaveTraceIDEmpty() {
        String actual = store.saveTraceID(request);

        assertTrue(StringUtils.isNotEmpty(actual));
    }

    @Test
    public void testSaveAuthSuccess() throws Exception {
        Auth auth = new Auth();

        when(serialiser.marshallAuth(auth))
                .thenReturn(authJson);

        store.saveAuth(auth);

        verify(serialiser, times(1)).marshallAuth(auth);
        assertThat(MDC.get(AUTH_KEY), equalTo(authJson));
    }

    @Test
    public void testSaveAuthError() throws Exception {
        MDC.put(TRACE_ID_KEY, TRACE_ID);
        Auth auth = new Auth();

        PrintStream stdErr = mock(PrintStream.class);
        System.setErr(stdErr);

        when(serialiser.marshallAuth(auth))
                .thenThrow(new LoggingException("bork"));

        ArgumentCaptor<LoggingException> captor = ArgumentCaptor.forClass(LoggingException.class);

        store.saveAuth(auth);

        verify(serialiser, times(1)).marshallAuth(auth);
        verify(stdErr, times(1)).println(captor.capture());
        assertThat(captor.getValue().getMessage(), equalTo(format(MARSHALL_ERR_FMT, AUTH_KEY, TRACE_ID)));
    }

    @Test
    public void testGetAuthSuccess() throws Exception {
        MDC.put(AUTH_KEY, authJson);
        Auth auth = new Auth();

        when(serialiser.unmarshallAuth(authJson))
                .thenReturn(auth);

        Auth result = store.getAuth();

        assertThat(result, equalTo(auth));
        verify(serialiser, times(1)).unmarshallAuth(authJson);
    }

    @Test
    public void testGetAuthNonFound() throws LoggingException {
        Auth result = store.getAuth();
        assertThat(result, is(nullValue()));
        verify(serialiser, never()).unmarshallAuth(anyString());
    }

    @Test
    public void testGetAuthUnmarshallError() throws Exception {
        MDC.put(AUTH_KEY, authJson);
        MDC.put(TRACE_ID_KEY, TRACE_ID);

        PrintStream stdErr = mock(PrintStream.class);
        System.setErr(stdErr);

        ArgumentCaptor<LoggingException> captor = ArgumentCaptor.forClass(LoggingException.class);

        when(serialiser.unmarshallAuth(authJson))
                .thenThrow(new LoggingException("bork"));

        Auth result = store.getAuth();

        assertThat(result, is(nullValue()));
        verify(stdErr, times(1)).println(captor.capture());
        verify(serialiser, times(1)).unmarshallAuth(authJson);
        assertThat(captor.getValue().getMessage(), equalTo(format(UNMARSHALL_ERR_FMT, AUTH_KEY, TRACE_ID)));
    }

    @Test
    public void saveTraceId_valueAlreadyStored_requestValueShouldOverride() {
        String existingValue = "0987654321";
        MDC.put(TRACE_ID_KEY, existingValue);

        String requestId = "1234567890";
        when(request.getHeader(REQUEST_ID_HEADER))
                .thenReturn(requestId);

        String actual = store.saveTraceID(request);

        assertThat(actual, equalTo(requestId));
    }

    @Test
    public void saveTraceId_noIDInRequestOrStore_shouldGenerateAndStoreNewID() {
        when(request.getParameter(TRACE_ID_KEY))
                .thenReturn("");

        String actual = store.saveTraceID(request);

        assertTrue(StringUtils.isNotEmpty(actual));
        UUID.fromString(actual);
    }

    @Test
    public void saveTraceId_RequestIDHeaderNoValueStored_shouldStoreRequestHeader() {
        String value = "1234567890";
        when(request.getHeader(REQUEST_ID_HEADER))
                .thenReturn(value);

        String actual = store.saveTraceID(request);

        assertThat(actual, equalTo(value));
    }

    @Test
    public void saveTraceId_emptyValueNoStoredValue_shouldGenerateAndStoreValue() {
        String actual = store.saveTraceID(request);

        assertTrue(StringUtils.isNotEmpty(actual));
    }

    @Test
    public void saveTraceId_paramEmptyAndValueStored_shouldOverwriteWithNewValue() {
        MDC.put(TRACE_ID_KEY, TRACE_ID);

        String actual = store.saveTraceID("");

        assertTrue(StringUtils.isNotEmpty(actual));
        assertThat(actual, not(equalTo(TRACE_ID)));

        // if this fails an exception will be thrown.
        UUID.fromString(actual);
    }

    @Test
    public void saveTraceId_paramNotEmptyAndValueStored_shouldOverwriteStoredValue() {
        MDC.put(TRACE_ID_KEY, TRACE_ID);

        String actual = store.saveTraceID("1234567890");

        assertThat(actual, equalTo("1234567890"));
    }

    @Test
    public void saveTraceId_paramNotEmptyAndStoreEmpty_shouldUseParamValue() {
        String actual = store.saveTraceID("1234567890");

        assertThat(actual, equalTo("1234567890"));
    }

    @Test
    public void saveTraceID_headerNullStoreNull_shouldGenerateNewID() {
        HttpUriRequest req = mock(HttpUriRequest.class);
        when(req.getFirstHeader(TRACE_ID_KEY))
                .thenReturn(null);

        String actual = store.saveTraceID(req);

        assertTrue(StringUtils.isNotEmpty(actual));
    }

    @Test
    public void saveTraceID_headerNullStoreValueExists_shouldOverwriteWithNewValue() {
        HttpUriRequest req = mock(HttpUriRequest.class);
        when(req.getFirstHeader(REQUEST_ID_HEADER))
                .thenReturn(null);

        MDC.put(TRACE_ID_KEY, TRACE_ID);

        String actual = store.saveTraceID(req);

        assertTrue(StringUtils.isNotEmpty(actual));
        assertTrue(!StringUtils.equals(actual, TRACE_ID));
    }


    @Test
    public void saveTraceID_headerAndStoreValuesExists_shouldOverwriteStoredValue() {
        String headerValue = "12345";
        Header header = mock(Header.class);
        when(header.getValue())
                .thenReturn(headerValue);

        HttpUriRequest req = mock(HttpUriRequest.class);
        when(req.getFirstHeader(REQUEST_ID_HEADER))
                .thenReturn(header);

        String existingValue = "098765";
        MDC.put(TRACE_ID_KEY, existingValue);

        String actual = store.saveTraceID(req);

        assertThat(actual, equalTo(headerValue));
    }

    @Test
    public void saveTraceID_HttpServletRequestIsNull_shouldGenerateNewId() {
        HttpServletRequest request = null;

        String actual = store.saveTraceID(request);

        assertTrue(StringUtils.isNotEmpty(actual));
    }

    @Test
    public void saveTraceID_HttpUriRequestIsNull_shouldGenerateNewId() {
        HttpUriRequest request = null;

        String actual = store.saveTraceID(request);

        assertTrue(StringUtils.isNotEmpty(actual));
    }
}
