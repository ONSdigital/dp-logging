package com.github.onsdigital.logging;

import com.github.onsdigital.logging.v2.DPLogger;
import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.config.Builder;
import com.github.onsdigital.logging.v2.config.Config;
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

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DPLoggerTest {

    @Mock
    private LogSerialiser serialiser;

    @Mock
    private PrintStream printStream;

    @Mock
    private Logger logger;

    @Before
    public void setup() throws LoggingException {
        when(logger.getName())
                .thenReturn("com.test");

        Config config = new Builder()
                .dataNamespace("com.test.data")
                .serialiser(serialiser)
                .logger(logger)
                .create();

        System.setOut(printStream);

        DPLogger.init(config);


    }

    @Test(expected = RuntimeException.class)
    public void testLogMarshalEventFails() throws Exception {
        LoggingException ex = new LoggingException("failed to marshal event to json");

        when(serialiser.toJson(any(SimpleEvent.class)))
                .thenThrow(ex);

        ArgumentCaptor<String> outArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SimpleEvent> eventArgumentCaptor = ArgumentCaptor.forClass(SimpleEvent.class);

        SimpleEvent event = new SimpleEvent("com.test", Severity.INFO);


        event.logAndThrow(new RuntimeException("caboom"), "something bad happebed");

        DPLogger.logIt(event);

        verify(serialiser, times(2)).toJson(eventArgumentCaptor.capture());
        verify(printStream).println(outArgumentCaptor.capture());

        assertThat(eventArgumentCaptor.getAllValues().get(0), equalTo(event));
        assertThat(eventArgumentCaptor.getAllValues().get(1).getThrowable(), equalTo(ex));
    }

    @Test(expected = RuntimeException.class)
    public void testLogAndThrow() throws Exception {
        try {
            throw new RuntimeException("one");
        } catch (Exception e) {
            error().logAndThrow(e, "caught and thrown");
        }
    }
}
