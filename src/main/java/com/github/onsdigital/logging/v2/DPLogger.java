package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.serializer.EventSerialiser;
import org.slf4j.Logger;

import static java.util.Objects.requireNonNull;

public class DPLogger {

    private static Logger LOGGER;
    private static EventSerialiser EVENT_SERIALISER;

    private DPLogger() {
        // contains only static only methods - hide constructor.
    }

    public static void init(Logger logger, EventSerialiser serialiser) {
        LOGGER = requireNonNull(logger);
        EVENT_SERIALISER = requireNonNull(serialiser);
    }

    public static Logger getLogger() {
        if (LOGGER == null) {
            throw new DPLoggerException("DPLogger has not been initialised: Logger is null");
        }
        return LOGGER;
    }

    public static EventSerialiser getEventSerialiser() {
        if (EVENT_SERIALISER == null) {
            throw new DPLoggerException("DPLogger has not been initialised: EventSerialiser is null");
        }
        return EVENT_SERIALISER;
    }

    public static class DPLoggerException extends RuntimeException {
        public DPLoggerException(String message) {
            super(message);
        }

        public DPLoggerException(String message, Throwable cause) {
            super(message, cause);
        }

        public DPLoggerException(Throwable cause) {
            super(cause);
        }

        public DPLoggerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
