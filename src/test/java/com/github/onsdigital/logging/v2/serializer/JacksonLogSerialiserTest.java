package com.github.onsdigital.logging.v2.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.config.Config;
import com.github.onsdigital.logging.v2.event.Auth;
import com.github.onsdigital.logging.v2.event.HTTP;
import com.github.onsdigital.logging.v2.event.Severity;
import com.github.onsdigital.logging.v2.event.SimpleEvent;
import com.github.onsdigital.logging.v2.storage.LogStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class JacksonLogSerialiserTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JsonProcessingException jsonProcessingException;

    @Mock
    private SimpleEvent errorEvent;

    @Mock
    private Config config;

    private LogSerialiser serialiser;
    private SimpleEvent event;
    private LoggingException loggingException;

    @Mock
    private ErrorEventMapper errorEventMapper;


    @Before
    public void setUp() {
        this.serialiser = new JacksonLogSerialiser(objectMapper, errorEventMapper);
        SimpleEvent expected = new SimpleEvent("test.test", Severity.INFO, mock(LogStore.class), "");
        loggingException = new LoggingException("bork");
    }

    @Test
    public void testMarshallEventSuccess() throws Exception {
        when(objectMapper.writeValueAsString(event))
                .thenReturn("JSON");

        String result = serialiser.marshallEvent(event);

        assertThat(result, equalTo("JSON"));
        verify(objectMapper, times(1)).writeValueAsString(event);
    }

    @Test(expected = LoggingException.class)
    public void testMarshallEventException() throws Exception {
        when(objectMapper.writeValueAsString(event))
                .thenThrow(jsonProcessingException);

        when(objectMapper.writeValueAsString(errorEvent))
                .thenThrow(jsonProcessingException);

        when(errorEventMapper.map(any(), any(), any()))
                .thenReturn(errorEvent);

        try {
            serialiser.marshallEvent(event);
        } catch (LoggingException ex) {
            verify(objectMapper, times(1)).writeValueAsString(event);
            verify(objectMapper, times(1)).writeValueAsString(errorEvent);
            throw ex;
        }
    }

    @Test
    public void testMarshalEvent_FirstAttemptFailure() throws Exception {
        when(objectMapper.writeValueAsString(event))
                .thenThrow(jsonProcessingException);

        when(objectMapper.writeValueAsString(errorEvent))
                .thenReturn("ERROR JSON");

        when(errorEventMapper.map(any(), any(), any()))
                .thenReturn(errorEvent);

        String result = serialiser.marshallEvent(event);

        assertThat(result, equalTo("ERROR JSON"));
        verify(objectMapper, times(1)).writeValueAsString(event);
        verify(objectMapper, times(1)).writeValueAsString(errorEvent);
        verify(errorEventMapper, times(1)).map(any(), any(), any());
    }

    @Test
    public void testUnmarshallHTTP_Success() throws Exception {
        HTTP http = mock(HTTP.class);

        when(objectMapper.readValue(anyString(), eq(HTTP.class)))
                .thenReturn(http);

        HTTP result = serialiser.unmarshallHTTP("HTTP JSON STRING");

        assertThat(result, equalTo(http));
        verify(objectMapper, times(1)).readValue("HTTP JSON STRING", HTTP.class);
    }

    @Test(expected = LoggingException.class)
    public void testUnmarshallHTTP_Error() throws Exception {
        HTTP http = mock(HTTP.class);

        when(objectMapper.readValue(anyString(), eq(HTTP.class)))
                .thenThrow(jsonProcessingException);

        try {
            serialiser.unmarshallHTTP("HTTP JSON STRING");
        } catch (LoggingException ex) {
            verify(objectMapper, times(1)).readValue("HTTP JSON STRING", HTTP.class);
            throw ex;
        }
    }

    @Test
    public void testMarshallHTTPSuccess() throws Exception {
        HTTP http = mock(HTTP.class);

        when(objectMapper.writeValueAsString(any(HTTP.class)))
                .thenReturn("JSON");

        String result = serialiser.marshallHTTP(http);

        assertThat(result, equalTo("JSON"));
        verify(objectMapper, times(1)).writeValueAsString(http);
    }

    @Test(expected = LoggingException.class)
    public void testMarshallHTTPError() throws Exception {
        HTTP http = mock(HTTP.class);

        when(objectMapper.writeValueAsString(any(HTTP.class)))
                .thenThrow(jsonProcessingException);

        try {
            serialiser.marshallHTTP(http);
        } catch (LoggingException ex) {
            verify(objectMapper, times(1)).writeValueAsString(http);
            throw ex;
        }
    }

    @Test
    public void testMarshallAuthSuccess() throws Exception {
        Auth auth = mock(Auth.class);

        when(objectMapper.writeValueAsString(auth))
                .thenReturn("JSON");

        String result = serialiser.marshallAuth(auth);

        assertThat(result, equalTo("JSON"));
        verify(objectMapper, times(1)).writeValueAsString(auth);
    }

    @Test(expected = LoggingException.class)
    public void testMarshallAuthError() throws Exception {
        Auth auth = mock(Auth.class);

        when(objectMapper.writeValueAsString(auth))
                .thenThrow(jsonProcessingException);

        try {
            serialiser.marshallAuth(auth);
        } catch (LoggingException ex) {
            verify(objectMapper, times(1)).writeValueAsString(auth);
            throw ex;
        }
    }

    @Test
    public void testUnmarshallAuthSuccess() throws Exception {
        Auth auth = mock(Auth.class);

        when(objectMapper.readValue("JSON", Auth.class))
                .thenReturn(auth);

        Auth result = serialiser.unmarshallAuth("JSON");

        assertThat(result, equalTo(auth));
        verify(objectMapper, times(1)).readValue("JSON", Auth.class);
    }

    @Test(expected = LoggingException.class)
    public void testUnarshallAuthError() throws Exception {
        Auth auth = mock(Auth.class);

        when(objectMapper.readValue("JSON", Auth.class))
                .thenThrow(jsonProcessingException);
        try {
            serialiser.unmarshallAuth("JSON");
        } catch (LoggingException ex) {
            verify(objectMapper, times(1)).readValue("JSON", Auth.class);
            throw ex;
        }
    }
}
