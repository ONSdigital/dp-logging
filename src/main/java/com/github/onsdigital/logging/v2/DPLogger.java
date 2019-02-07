package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.config.Logger;
import com.github.onsdigital.logging.v2.config.nop.LogConfig;
import com.github.onsdigital.logging.v2.event.BaseEvent;

import java.time.format.DateTimeFormatter;

import static java.text.MessageFormat.format;

public class DPLogger {

    private static LogConfig CONFIG = null;

    private static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    static final String MARSHAL_FAILURE = "failed to marshal log event to json: event {0}, exception: {1}";
    static final String MARSHALL_EVENT_ERR = "error marshalling dp log event to json";

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(ISO8601_FORMAT);

    private DPLogger() {
        // contains only static only methods - hide constructor.
    }

    public static void init(LogConfig config) {
        if (CONFIG == null) {
            synchronized (DPLogger.class) {
                if (CONFIG == null) {
                    CONFIG = config;
                }
            }
        }
    }

    static void reload(LogConfig config) {
        synchronized (DPLogger.class) {
            CONFIG = config;
        }
    }

    public static LogConfig logConfig() {
        if (CONFIG == null) {
            throw new UncheckedLoggingException("DPLogger is not initalised");
        }
        return CONFIG;
    }

    public static <T extends BaseEvent> void log(T event) {
        Logger logger = CONFIG.getLogger();
        try {
            logger.info(logConfig().getSerialiser().marshallEvent(event));
        } catch (LoggingException ex) {
            System.out.println(format(MARSHAL_FAILURE, event, ex));
            if (System.out.checkError()) {
                logConfig().getShutdownHook().shutdown();
            }
        }
    }

    public static DateTimeFormatter formatter() {
        return FORMATTER;
    }
}