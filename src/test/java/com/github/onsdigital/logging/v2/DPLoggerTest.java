package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.config.Config;
import com.github.onsdigital.logging.v2.config.ShutdownHook;
import com.github.onsdigital.logging.v2.event.Severity;
import com.github.onsdigital.logging.v2.event.SimpleEvent;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.io.PrintStream;

import static com.github.onsdigital.logging.v2.DPLogger.MARSHALL_EVENT_ERR;
import static com.github.onsdigital.logging.v2.DPLogger.MARSHAL_FAILURE;
import static java.text.MessageFormat.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DPLoggerTest {

    @Mock
    private LogSerialiser serialiser;

    @Mock
    private PrintStream printStream;

    @Mock
    private ShutdownHook shutdownHook;

    @Mock
    private Logger logger;

    @Mock
    private Config config;

    @Before
    public void setUp() throws LoggingException {

        when(config.getNamespace()).thenReturn("com.test");
        when(config.getSerialiser()).thenReturn(serialiser);
        when(config.getLogger()).thenReturn(logger);
        when(config.getShutdownHook()).thenReturn(shutdownHook);

        System.setOut(printStream);

        DPLogger.reload(config);
    }

    @Test
    public void testLogSuccessful() throws Exception {
        SimpleEvent event = new SimpleEvent("com.test", Severity.INFO, "One o'clock and all's well"); //Disney referernce :p
        String json = "{\"json\": \"value\"}";

        when(serialiser.toJson(event))
                .thenReturn(json);

        ArgumentCaptor<SimpleEvent> toJsonCaptor = ArgumentCaptor.forClass(SimpleEvent.class);
        ArgumentCaptor<String> logInfoCaptor = ArgumentCaptor.forClass(String.class);

        DPLogger.log(event);

        verify(config, times(1)).getLogger();
        verify(serialiser, times(1)).toJson(toJsonCaptor.capture());
        verify(logger, times(1)).info(logInfoCaptor.capture());
        verify(config, never()).getShutdownHook();
        verifyZeroInteractions(printStream, shutdownHook);
    }

    /**
     * Test scenario where marshalling the event fails, but retrying mashalling the error event is successful.
     */
    @Test
    public void testLogMarshalFailsFirstAttempt() throws Exception {
        SimpleEvent event = new SimpleEvent("com.test", Severity.INFO, "Get to da choppa!");
        LoggingException ex = new LoggingException("failed to marshal event to json");
        String json = "{\"json\": \"value\"}";

        when(serialiser.toJson(any(SimpleEvent.class)))
                .thenThrow(ex)
                .thenReturn(json);

        ArgumentCaptor<SimpleEvent> eventArgumentCaptor = ArgumentCaptor.forClass(SimpleEvent.class);

        DPLogger.log(event);

        verify(config, times(1)).getLogger();
        verify(serialiser, times(2)).toJson(eventArgumentCaptor.capture());
        verify(logger, times(1)).info(json);
        verifyZeroInteractions(shutdownHook, printStream);

        SimpleEvent first = eventArgumentCaptor.getAllValues().get(0);
        assertThat(first, equalTo(event));

        SimpleEvent second = eventArgumentCaptor.getAllValues().get(1);
        assertThat(second.getNamespace(), equalTo("com.test"));
        assertThat(second.getSeverity(), equalTo(Severity.ERROR.getLevel()));
        assertThat(second.getEvent(), equalTo(MARSHALL_EVENT_ERR));
        assertThat(second.getThrowable(), equalTo(ex));
        assertThat(second.getData().containsKey("event"), is(true));
        assertThat(second.getData().get("event", String.class), equalTo(first.toString()));

    }

    /**
     * Test scenrio where marshal event fails, retry marshall error event fails but the exception is sent to stdout
     * successfully.
     */
    @Test
    public void testLogMarshalEventFailsAllAttempts() throws Exception {
        LoggingException ex = new LoggingException("failed to marshal event to json");

        when(serialiser.toJson(any(SimpleEvent.class)))
                .thenThrow(ex)
                .thenThrow(ex);

        when(printStream.checkError())
                .thenReturn(false);

        ArgumentCaptor<String> outArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SimpleEvent> eventArgumentCaptor = ArgumentCaptor.forClass(SimpleEvent.class);

        SimpleEvent event = new SimpleEvent("com.test", Severity.INFO);

        // run it.
        DPLogger.log(event);

        verify(serialiser, times(2)).toJson(eventArgumentCaptor.capture());
        verify(printStream).println(outArgumentCaptor.capture());

        SimpleEvent first = eventArgumentCaptor.getAllValues().get(0);
        assertThat(first, equalTo(event));

        SimpleEvent second = eventArgumentCaptor.getAllValues().get(1);

        assertThat(second.getNamespace(), equalTo("com.test"));
        assertThat(second.getSeverity(), equalTo(Severity.ERROR.getLevel()));
        assertThat(second.getEvent(), equalTo(MARSHALL_EVENT_ERR));
        assertThat(second.getThrowable(), equalTo(ex));
        assertThat(second.getData().containsKey("event"), is(true));
        assertThat(second.getData().get("event", String.class), equalTo(first.toString()));

        verify(printStream, times(1)).println(format(MARSHAL_FAILURE, event, ex));
        verify(printStream, times(1)).checkError();
        verify(shutdownHook, never()).shutdown();
    }

    /**
     * Test scenrio where all attempts to marshal fail and writing to stdout also fails. In this case the shutdown
     * hook should be invoked.
     */
    @Test
    public void testEpicFailure() throws Exception {
        LoggingException ex = new LoggingException("failed to marshal event to json");

        when(serialiser.toJson(any(SimpleEvent.class)))
                .thenThrow(ex)
                .thenThrow(ex);

        when(printStream.checkError())
                .thenReturn(true);

        ArgumentCaptor<String> outArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SimpleEvent> eventArgumentCaptor = ArgumentCaptor.forClass(SimpleEvent.class);

        SimpleEvent event = new SimpleEvent("com.test", Severity.INFO);

        // run it.
        DPLogger.log(event);

        verify(serialiser, times(2)).toJson(eventArgumentCaptor.capture());
        verify(printStream).println(outArgumentCaptor.capture());

        SimpleEvent first = eventArgumentCaptor.getAllValues().get(0);
        assertThat(first, equalTo(event));

        SimpleEvent second = eventArgumentCaptor.getAllValues().get(1);

        assertThat(second.getNamespace(), equalTo("com.test"));
        assertThat(second.getSeverity(), equalTo(Severity.ERROR.getLevel()));
        assertThat(second.getEvent(), equalTo(MARSHALL_EVENT_ERR));
        assertThat(second.getThrowable(), equalTo(ex));
        assertThat(second.getData().containsKey("event"), is(true));
        assertThat(second.getData().get("event", String.class), equalTo(first.toString()));

        verify(printStream, times(1)).println(format(MARSHAL_FAILURE, event, ex));
        verify(printStream, times(1)).checkError();
        verify(shutdownHook, times(1)).shutdown();
    }

    @Test
    public void testMarshallSuccessful() throws Exception {
        SimpleEvent event = new SimpleEvent("com.test", Severity.INFO, "Get to da choppa!");
        String json = "{\"json\": \"value\"}";

        when(serialiser.toJson(any(SimpleEvent.class))).thenReturn(json);
        ArgumentCaptor<SimpleEvent> eventCaptor = ArgumentCaptor.forClass(SimpleEvent.class);

        String result = DPLogger.marshal(event, true);

        verify(serialiser, times(1)).toJson(eventCaptor.capture());
        verify(config, times(1)).getSerialiser();
        assertThat(eventCaptor.getValue(), equalTo(event));
        assertThat(result, equalTo(json));
    }

    /**
     * Test scenario where attempting to marshall an event fails but the retry is succesful.
     */
    @Test
    public void testMarshallFailAndRetrySuccessful() throws Exception {
        LoggingException ex = new LoggingException("bork");
        SimpleEvent event = new SimpleEvent("com.test", Severity.INFO, "Get to da choppa!");
        String json = "{\"json\": \"value\"}";

        when(serialiser.toJson(any(SimpleEvent.class)))
                .thenThrow(ex)
                .thenReturn(json);

        ArgumentCaptor<SimpleEvent> eventCaptor = ArgumentCaptor.forClass(SimpleEvent.class);

        String result = DPLogger.marshal(event, true);

        verify(serialiser, times(2)).toJson(eventCaptor.capture());
        verify(config, times(2)).getSerialiser();
        assertThat(result, equalTo(json));

        assertThat(eventCaptor.getAllValues().get(0), equalTo(event));

        SimpleEvent second = eventCaptor.getAllValues().get(1);
        assertThat(second.getNamespace(), equalTo("com.test"));
        assertThat(second.getSeverity(), equalTo(Severity.ERROR.getLevel()));
        assertThat(second.getEvent(), equalTo(MARSHALL_EVENT_ERR));
        assertThat(second.getThrowable(), equalTo(ex));
        assertThat(second.getData().containsKey("event"), is(true));
        assertThat(second.getData().get("event", String.class), equalTo(event.toString()));
    }

    @Test(expected = LoggingException.class)
    public void testMarshalFailNoRetry() throws Exception {
        LoggingException ex = new LoggingException("bork");
        SimpleEvent event = new SimpleEvent("com.test", Severity.INFO, "Get to da choppa!");
        String json = "{\"json\": \"value\"}";

        when(serialiser.toJson(any(SimpleEvent.class))).thenThrow(ex);

        ArgumentCaptor<SimpleEvent> eventCaptor = ArgumentCaptor.forClass(SimpleEvent.class);

        try {
            DPLogger.marshal(event, false);
        } catch (LoggingException e) {
            verify(serialiser, times(1)).toJson(eventCaptor.capture());
            verify(config, times(1)).getSerialiser();
            assertThat(eventCaptor.getValue(), equalTo(event));
            throw e;
        }
    }
}
