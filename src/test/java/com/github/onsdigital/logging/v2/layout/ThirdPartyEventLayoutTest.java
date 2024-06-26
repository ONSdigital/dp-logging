package com.github.onsdigital.logging.v2.layout;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.event.Severity;
import com.github.onsdigital.logging.v2.event.ThirdPartyEvent;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import com.github.onsdigital.logging.v2.storage.LogStore;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ThirdPartyEventLayoutTest {

    @Mock
    private ILoggingEvent mockEvent;

    @Mock
    private LogSerialiser logSerialiserMock;

    @Mock
    private LogStore logStore;

    private ThirdPartyEventLayout layout;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.layout = new ThirdPartyEventLayout(() -> logSerialiserMock, () -> logStore);
    }

    @Test
    public void testLayoutSuccess() throws LoggingException {
        when(mockEvent.getLoggerName())
                .thenReturn("test.logger.name");
        when(mockEvent.getLevel())
                .thenReturn(Level.INFO);
        when(mockEvent.getFormattedMessage())
                .thenReturn("hello world");

        when(logSerialiserMock.marshallEvent(any(ThirdPartyEvent.class)))
                .thenReturn("done");

        ArgumentCaptor<ThirdPartyEvent> argumentCaptor = ArgumentCaptor.forClass(ThirdPartyEvent.class);
        when(logSerialiserMock.marshallEvent(argumentCaptor.capture())).thenReturn("done");

        layout.doLayout(mockEvent);

        ThirdPartyEvent e = argumentCaptor.getValue();
        assertThat("event incorrect", e.getEvent(), equalTo("external library event test.logger.name"));
        assertThat("event incorrect", e.getRaw(), equalTo("hello world"));
        assertThat("namespace incorrect", e.getNamespace(), equalTo("dp-logger-default"));
        assertThat("severity incorrect", e.getSeverity(), equalTo(Severity.INFO.getLevel()));
        assertThat("data incorrect", e.getData().isEmpty(), is(true));
        assertNotNull("created_at incorrect", e.getCreateAt());
        assertNull("http incorrect", e.getHttp());
        assertNull("auth incorrect", e.getAuth());
        assertNull("error incorrect", e.getErrors());
        assertNull("trace_id incorrect", e.getTraceID());
        assertNull("span_id incorrect", e.getSpanID());

        verify(logSerialiserMock, times(1)).marshallEvent(e);
    }

    @Test
    public void testLayoutToJsonFailure() throws Exception {
        when(mockEvent.getLoggerName())
                .thenReturn("test.logger.name");
        when(mockEvent.getLevel())
                .thenReturn(Level.INFO);
        when(mockEvent.getFormattedMessage())
                .thenReturn("hello world");

        LoggingException ex = new LoggingException("bork");

        ArgumentCaptor<ThirdPartyEvent> eventCaptor = ArgumentCaptor.forClass(ThirdPartyEvent.class);
        when(logSerialiserMock.marshallEvent(eventCaptor.capture()))
                .thenThrow(ex);

        String result = layout.doLayout(mockEvent);

        assertThat(result, equalTo("error while attempting to marshallHTTP log event to json: bork"));

        ThirdPartyEvent e = eventCaptor.getValue();
        assertThat("event incorrect", e.getEvent(), equalTo("external library event test.logger.name"));
        assertThat("event incorrect", e.getRaw(), equalTo("hello world"));
        assertThat("namespace incorrect", e.getNamespace(), equalTo("dp-logger-default"));
        assertThat("severity incorrect", e.getSeverity(), equalTo(Severity.INFO.getLevel()));
        assertThat("data incorrect", e.getData().isEmpty(), is(true));
        assertNotNull("created_at incorrect", e.getCreateAt());
        assertNull("http incorrect", e.getHttp());
        assertNull("auth incorrect", e.getAuth());
        assertNull("error incorrect", e.getErrors());
        assertNull("trace_id incorrect", e.getTraceID());
        assertNull("span_id incorrect", e.getSpanID());

        verify(logSerialiserMock, times(1)).marshallEvent(e);
    }

}
