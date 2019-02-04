package com.github.onsdigital.logging.v2.layout;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.onsdigital.logging.v2.DPLogger;
import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.event.Severity;
import com.github.onsdigital.logging.v2.event.ThirdPartyEvent;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;

import java.util.function.Supplier;

import static com.github.onsdigital.logging.v2.DPLogger.logConfig;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;

public class ThirdPartyEventLayout extends PatternLayout {

    private Supplier<LogSerialiser> serialiserSupplier;

    public ThirdPartyEventLayout() {
        this.serialiserSupplier = () -> logConfig().getSerialiser();
    }

    ThirdPartyEventLayout(Supplier<LogSerialiser> serialiserSupplier) {
        this.serialiserSupplier = serialiserSupplier;
    }

    @Override
    public String doLayout(ILoggingEvent e) {
        ThirdPartyEvent wrapper = new ThirdPartyEvent(e.getLoggerName(), toSeverity(e.getLevel()),
                e.getFormattedMessage());
        try {
            return serialiserSupplier.get().toJson(wrapper) + "\n";
        } catch (LoggingException ex) {
            try {
                return serialiserSupplier.get().toJson(error().exception(ex));
            } catch (LoggingException ex2) {
                DPLogger.logConfig().getShutdownHook().shutdown();
                return null;
            }
        }
    }

    private Severity toSeverity(Level level) {
        if (level.toInteger() == Level.ERROR_INT) {
            return Severity.ERROR;
        }

        if (level.toInteger() == Level.WARN_INT) {
            return Severity.WARN;
        }
        return Severity.INFO;
    }
}
