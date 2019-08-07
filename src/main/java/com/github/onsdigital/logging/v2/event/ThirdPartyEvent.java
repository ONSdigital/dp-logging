package com.github.onsdigital.logging.v2.event;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.github.onsdigital.logging.v2.storage.LogStore;

public class ThirdPartyEvent extends BaseEvent {

    private String raw;

    public ThirdPartyEvent(String namespace, Severity severity, String raw, LogStore logStore) {
        super(namespace, severity, logStore);
        super.event = "third party log";
        this.raw = raw;
    }

    public ThirdPartyEvent(String namespace, Severity severity, ILoggingEvent e, LogStore logStore) {
        super(namespace, severity, logStore);
        super.event = "third party log";
        this.raw = e.getFormattedMessage();
        this.traceID(logStore.getTraceID());

        if (e != null && e.getThrowableProxy() != null) {
            IThrowableProxy iThrowableProxy = e.getThrowableProxy();
            if (iThrowableProxy instanceof ThrowableProxy) {
                Throwable t = ((ThrowableProxy) iThrowableProxy).getThrowable();
                // TODO - once the centralised logging schema is updated this needs to use the recurive method to
                // capture the full error.
                this.exception(t);
            }
        }
    }

    public String getRaw() {
        return this.raw;
    }
}
