package com.github.onsdigital.logging.v2.layout;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.event.ThirdPartyEvent;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import com.github.onsdigital.logging.v2.storage.LogStore;

import java.util.function.Supplier;

import static com.github.onsdigital.logging.v2.DPLogger.logConfig;
import static com.github.onsdigital.logging.v2.event.Severity.getSeverity;
import static java.text.MessageFormat.format;

public class ThirdPartyEventLayout extends PatternLayout {

    private Supplier<LogSerialiser> serialiserSupplier;
    private Supplier<LogStore> logStoreSupplier;

    public ThirdPartyEventLayout() {
        this.serialiserSupplier = () -> logConfig().getSerialiser();
        this.logStoreSupplier = () -> logConfig().getLogStore();
    }

    ThirdPartyEventLayout(Supplier<LogSerialiser> serialiserSupplier, Supplier<LogStore> logStoreSupplier) {
        this.serialiserSupplier = serialiserSupplier;
        this.logStoreSupplier = logStoreSupplier;
    }

    @Override
    public String doLayout(ILoggingEvent e) {
        ThirdPartyEvent wrapper = new ThirdPartyEvent(e.getLoggerName(), getSeverity(e.getLevel()),
                e, logStoreSupplier.get());
        try {
            return serialiserSupplier.get().marshallEvent(wrapper) + "\n";
        } catch (LoggingException ex) {
            return format("error while attempting to marshallHTTP log event to json: {0}", ex.getMessage());
        }
    }
}
