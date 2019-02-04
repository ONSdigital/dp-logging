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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DPLoggerTest {

    @Mock
    private LogSerialiser serialiser;

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

        DPLogger.init(config);
    }

    @Test
    public void testLogMarshalEventFails() throws Exception {
        when(serialiser.toJson(any(SimpleEvent.class)))
                .thenThrow(new LoggingException("failed to marshal event to json"));

        DPLogger.logIt(new SimpleEvent("com.test", Severity.INFO));
    }
}
