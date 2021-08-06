package com.github.onsdigital.logging.v2.event;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.github.onsdigital.logging.v2.storage.LogStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

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
        Throwable bar = new RuntimeException("bar");
        Throwable foo = new RuntimeException("foo", bar);
        when(event.getThrowableProxy())
                .thenReturn(throwableProxy);
        when(throwableProxy.getThrowable())
                .thenReturn(foo);

        ThirdPartyEvent thirdPartyEvent = new ThirdPartyEvent("", Severity.ERROR, event, logStore);
        List<Error> actual = thirdPartyEvent.getErrors();

        Errors expected = new Errors(foo);
        String expectedMessage = foo.getClass().getName() + ": " + foo.getMessage();
        assertThat(actual.size(), equalTo(2));
        assertThat(actual.get(0).getMessage(), equalTo(expectedMessage));

        StackTrace[] expectedStackTrace = stackTraceArrayFromThrowable(foo);
        assertThat(actual.get(0).getStackTraces(), equalTo(expectedStackTrace));
    }
}
