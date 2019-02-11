package com.github.onsdigital.logging.v2.storage;

import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.event.Auth;
import com.github.onsdigital.logging.v2.event.HTTP;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import org.apache.commons.lang3.StringUtils;
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

import static com.github.onsdigital.logging.v2.storage.MDCLogStore.AUTH_KEY;
import static com.github.onsdigital.logging.v2.storage.MDCLogStore.HTTP_KEY;
import static com.github.onsdigital.logging.v2.storage.MDCLogStore.MARSHALL_ERR_FMT;
import static com.github.onsdigital.logging.v2.storage.MDCLogStore.TRACE_ID_KEY;
import static com.github.onsdigital.logging.v2.storage.MDCLogStore.UNMARSHALL_ERR_FMT;
import static java.text.MessageFormat.format;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
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
    public void testSaveHTTPSuccess() throws Exception {
        HTTP http = new HTTP();

        when(serialiser.marshallHTTP(http))
                .thenReturn(httpJson);

        store.saveHTTP(http);

        verify(serialiser, times(1)).marshallHTTP(http);
        assertThat(MDC.get(HTTP_KEY), equalTo(httpJson));
    }

    @Test
    public void testSaveHTTPMarshallError() throws Exception {
        HTTP http = new HTTP();
        PrintStream stdErr = mock(PrintStream.class);
        System.setErr(stdErr);

        when(request.getHeader("trace_id")).thenReturn(TRACE_ID);
        store.saveTraceID(request);

        when(serialiser.marshallHTTP(http))
                .thenThrow(new LoggingException("bork"));

        store.saveHTTP(http);

        ArgumentCaptor<LoggingException> captor = ArgumentCaptor.forClass(LoggingException.class);

        verify(serialiser, times(1)).marshallHTTP(http);
        verify(stdErr, times(1)).println(captor.capture());

        LoggingException actual = captor.getValue();
        assertThat(actual.getMessage(), equalTo(format(MARSHALL_ERR_FMT, HTTP_KEY, TRACE_ID)));
    }

    @Test
    public void testGetHTTPSuccess() throws Exception {
        MDC.put(HTTP_KEY, httpJson);

        HTTP http = new HTTP();

        when(serialiser.unmarshallHTTP(httpJson))
                .thenReturn(http);

        HTTP result = store.getHTTP();

        verify(serialiser, times(1)).unmarshallHTTP(httpJson);
        assertThat(result, equalTo(http));
    }

    @Test
    public void testGetHTTPSerialiserException() throws Exception {
        MDC.put(HTTP_KEY, httpJson);
        MDC.put(TRACE_ID_KEY, TRACE_ID);

        PrintStream stdErr = mock(PrintStream.class);
        System.setErr(stdErr);

        LoggingException exception = new LoggingException("bork");

        when(serialiser.unmarshallHTTP(httpJson))
                .thenThrow(exception);

        ArgumentCaptor<LoggingException> captor = ArgumentCaptor.forClass(LoggingException.class);

        HTTP result = store.getHTTP();

        verify(serialiser, times(1)).unmarshallHTTP(httpJson);
        verify(stdErr, times(1)).println(captor.capture());

        assertThat(result, is(nullValue()));
        assertThat(captor.getValue().getMessage(), equalTo(format(UNMARSHALL_ERR_FMT, HTTP_KEY, TRACE_ID)));
    }

    @Test
    public void testGetHTTPNoValueSavedS() throws Exception {
        PrintStream stdErr = mock(PrintStream.class);
        System.setErr(stdErr);

        HTTP result = store.getHTTP();

        assertThat(result, is(nullValue()));

        verifyZeroInteractions(serialiser, stdErr);
    }

    @Test
    public void testGetTraceIDSuccess() {
        MDC.put(TRACE_ID_KEY, TRACE_ID);
        assertThat(store.getTraceID(), equalTo(TRACE_ID));
    }

    @Test
    public void testSaveTraceIDSuccess() {
        when(request.getHeader(TRACE_ID_KEY)).thenReturn(TRACE_ID);

        store.saveTraceID(request);

        assertThat(MDC.get(TRACE_ID_KEY), equalTo(TRACE_ID));
    }

    @Test
    public void testSaveTraceIDEmpty() {
        store.saveTraceID(request);

        assertTrue(StringUtils.isNotEmpty(MDC.get(TRACE_ID_KEY)));
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
}
