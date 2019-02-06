package com.github.onsdigital.logging.v2.serialiser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.logging.v2.DPLogger;
import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.config.Config;
import com.github.onsdigital.logging.v2.event.Severity;
import com.github.onsdigital.logging.v2.event.SimpleEvent;
import com.github.onsdigital.logging.v2.serializer.JacksonLogSerialiser;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
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
    private Config config;

    private LogSerialiser serialiser;
    private SimpleEvent event;
    private LoggingException loggingException;

    @Before
    public void setUp() throws Exception {
        this.serialiser = new JacksonLogSerialiser(objectMapper);
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
                .thenThrow(jsonProcessingException)
                .thenReturn("ERROR JSON");

        when(config.getNamespace()).thenReturn("test");

        DPLogger.init(config);

        SimpleEvent err = error("failed to marshal log event to json")
                .exception(jsonProcessingException)
                .data("event", event.toString());

        String result = serialiser.marshallWithRetry(event);

        assertThat(result, equalTo("ERROR JSON"));
        verify(objectMapper, times(1)).writeValueAsString(event);
        verify(objectMapper, times(1)).writeValueAsString(err);
    }
}
