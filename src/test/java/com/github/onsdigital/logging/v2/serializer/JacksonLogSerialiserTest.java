package com.github.onsdigital.logging.v2.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.config.Config;
import com.github.onsdigital.logging.v2.event.Severity;
import com.github.onsdigital.logging.v2.event.SimpleEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
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
        SimpleEvent expected = new SimpleEvent("test.test", Severity.INFO);
        loggingException = new LoggingException("bork");
    }

    @Test
    public void testMarshallSimpleEventSuccess() throws Exception {
        when(objectMapper.writeValueAsString(event))
                .thenReturn("JSON");

        String result = serialiser.marshall(event);

        assertThat(result, equalTo("JSON"));
        verify(objectMapper, times(1)).writeValueAsString(event);
    }

    @Test(expected = LoggingException.class)
    public void testMarshallMapperException() throws Exception {
        when(objectMapper.writeValueAsString(event))
                .thenThrow(jsonProcessingException);

        try {
            serialiser.marshall(event);
        } catch (LoggingException ex) {
            verify(objectMapper, times(1)).writeValueAsString(event);
            throw ex;
        }
    }

    @Test
    public void testMarshallWithRetrySimpleEvent_Success() throws Exception {
        when(objectMapper.writeValueAsString(event))
                .thenReturn("JSON");

        String result = serialiser.marshallWithRetry(event);

        assertThat(result, equalTo("JSON"));
        verify(objectMapper, times(1)).writeValueAsString(event);
    }

    @Test
    public void testMarshallWithRetrySimpleEvent_FirstAttemptFailure() throws Exception {
        when(objectMapper.writeValueAsString(event))
                .thenThrow(jsonProcessingException);

        when(objectMapper.writeValueAsString(errorEvent))
                .thenReturn("ERROR JSON");

        when(errorEventMapper.map(any(), any(), any()))
                .thenReturn(errorEvent);

        String result = serialiser.marshallWithRetry(event);

        assertThat(result, equalTo("ERROR JSON"));
        verify(objectMapper, times(1)).writeValueAsString(event);
        verify(objectMapper, times(1)).writeValueAsString(errorEvent);
        verify(errorEventMapper, times(1)).map(any(), any(), any());
    }
}
