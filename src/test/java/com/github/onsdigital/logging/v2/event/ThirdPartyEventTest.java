package com.github.onsdigital.logging.v2.event;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.github.onsdigital.logging.v2.storage.LogStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.github.onsdigital.logging.v2.event.StackTrace.stackTraceArrayFromThrowable;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ThirdPartyEventTest {

    @Mock
    private ILoggingEvent event;

    @Mock
    private ThrowableProxy throwableProxy;

    @Mock
    private LogStore logStore;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldAddExceptionIfExists() {
        Throwable thrownException = new RuntimeException("nargle");

        when(event.getThrowableProxy())
                .thenReturn(throwableProxy);
        when(throwableProxy.getThrowable())
                .thenReturn(thrownException);

        ThirdPartyEvent thirdPartyEvent = new ThirdPartyEvent("", Severity.ERROR, event, logStore);
        Error actual = thirdPartyEvent.getError();

        String expectedMessage = thrownException.getClass().getName() + ": " + thrownException.getMessage();
        assertThat(actual.getMessage(), equalTo(expectedMessage));

        StackTrace[] expectedStackTrace = stackTraceArrayFromThrowable(thrownException);
        assertThat(actual.getStackTraces(), equalTo(expectedStackTrace));
    }
}
