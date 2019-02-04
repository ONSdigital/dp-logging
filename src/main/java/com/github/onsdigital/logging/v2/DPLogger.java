package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.config.Config;
import com.github.onsdigital.logging.v2.event.BaseEvent;
import com.github.onsdigital.logging.v2.event.SimpleEvent;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;

public class DPLogger {

    private static Config CONFIG = null;

    private static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String MARSHAL_FAILURE = "failed to marshal log event to json: event {0}, exception: {1}";

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(ISO8601_FORMAT);

    private DPLogger() {
        // contains only static only methods - hide constructor.
    }

    public static void init(Config config) {
        if (CONFIG == null) {
            synchronized (DPLogger.class) {
                if (CONFIG == null) {
                    CONFIG = config;
                }
            }
        }
    }

    public static Config logConfig() {
        if (CONFIG == null) {
            throw new UncheckedLoggingException("DPLogger is not initalised");
        }
        return CONFIG;
    }

    public static <T extends BaseEvent> void log(T event) {
        try {
            marshalEvent(event);
        } catch (LoggingException e) {
            try {
                marshalEvent(error().exception(e));
            } catch (LoggingException ex) {
                System.out.println(MessageFormat.format("failed to marshall error event {0}", ex));
                if (System.out.checkError()) {
                    CONFIG.getShutdownHook().shutdown();
                }
            }
        }
    }

    public static <T extends BaseEvent> void logIt(T event) {
        Logger logger = CONFIG.getLogger();
        try {
            logger.info(marshal(event, true));
        } catch (LoggingException ex) {
            System.out.println(MessageFormat.format(MARSHAL_FAILURE, event, ex));
            if (System.out.checkError()) {
                logConfig().getShutdownHook().shutdown();
            }
        }
    }


    static <T extends BaseEvent> String marshal(T event, boolean retry) throws LoggingException {
        try {
            return logConfig().getSerialiser().toJson(event);
        } catch (LoggingException ex) {
            if (retry) {
                SimpleEvent err = error().exception(ex);
                return marshal(err, false);
            }
            throw ex;
        }
    }


    private static <T extends BaseEvent> String marshalEvent(T event) throws LoggingException {
        try {
            return CONFIG.getSerialiser().toJson(event);
        } catch (Exception e) {
            throw new LoggingException("error marshalling log event to json", e);
        }
    }

    public static DateTimeFormatter formatter() {
        return FORMATTER;
    }
}

