package com.github.onsdigital.logging.v2.event;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.github.onsdigital.logging.v2.storage.LogStore;

import static com.github.onsdigital.logging.v2.DPLogger.logConfig;
import static java.text.MessageFormat.format;

public class ThirdPartyEvent extends BaseEvent {

    static final String EVENT_DESCRIPTION = "external library event";
    static final String NAMESPACE_FMT = "{0} {1}";
    private String raw;

    public ThirdPartyEvent(String namespace, Severity severity, String raw, LogStore logStore) {
        super(logConfig().getNamespace(), severity, logStore);
        super.event = formatEvent(namespace);
        this.traceID(logStore.getTraceID());
        this.raw = raw;
    }

    public ThirdPartyEvent(String namespace, Severity severity, ILoggingEvent e, LogStore logStore) {
        super(logConfig().getNamespace(), severity, logStore);
        super.event = formatEvent(namespace);
        this.raw = e.getFormattedMessage();
        this.traceID(logStore.getTraceID());

        if (e != null && e.getThrowableProxy() != null) {
            IThrowableProxy iThrowableProxy = e.getThrowableProxy();
            if (iThrowableProxy instanceof ThrowableProxy) {
                Throwable t = ((ThrowableProxy) iThrowableProxy).getThrowable();
                this.exception(t);
            }
        }
    }

    static String formatEvent(String eventNamespace) {
        return format(NAMESPACE_FMT, EVENT_DESCRIPTION, eventNamespace);
    }

    public String getRaw() {
        return this.raw;
    }
}
