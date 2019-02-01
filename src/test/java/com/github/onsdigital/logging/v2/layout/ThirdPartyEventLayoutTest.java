package com.github.onsdigital.logging.v2.layout;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.onsdigital.logging.v2.event.Severity;
import com.github.onsdigital.logging.v2.event.ThirdPartyEvent;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ThirdPartyEventLayoutTest {

    @Mock
    private ILoggingEvent mockEvent;

    @Mock
    private LogSerialiser logSerialiserMock;

    private ThirdPartyEventLayout layout;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.layout = new ThirdPartyEventLayout(() -> logSerialiserMock);
    }

    @Test
    public void testLayout() {
        when(mockEvent.getLoggerName())
                .thenReturn("test.logger.name");
        when(mockEvent.getLevel())
                .thenReturn(Level.INFO);
        when(mockEvent.getFormattedMessage())
                .thenReturn("hello world");

        when(logSerialiserMock.toJson(any(ThirdPartyEvent.class)))
                .thenReturn("done");

        ArgumentCaptor<ThirdPartyEvent> argumentCaptor = ArgumentCaptor.forClass(ThirdPartyEvent.class);
        when(logSerialiserMock.toJson(argumentCaptor.capture())).thenReturn("done");

        layout.doLayout(mockEvent);

        ThirdPartyEvent e = argumentCaptor.getValue();
        assertThat("event incorrect", e.getEvent(), equalTo("third party log"));
        assertThat("event incorrect", e.getRaw(), equalTo("hello world"));
        assertThat("namespace incorrect", e.getNamespace(), equalTo("test.logger.name"));
        assertThat("severity incorrect", e.getSeverity(), equalTo(Severity.INFO.getLevel()));
        assertThat("data incorrect", e.getData().isEmpty(), is(true));
        assertNotNull("created_at incorrect", e.getCreateAt());
        assertNull("http incorrect", e.getHttp());
        assertNull("auth incorrect", e.getAuth());
        assertNull("error incorrect", e.getError());
        assertNull("trace_id incorrect", e.getTraceID());
        assertNull("span_id incorrect", e.getSpanID());
        assertNull("throwable incorrect", e.getThrowable());

        verify(logSerialiserMock, times(1)).toJson(any(ThirdPartyEvent.class));
    }
}
