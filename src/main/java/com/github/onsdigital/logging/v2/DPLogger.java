package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.config.LoggerConfig;
import com.github.onsdigital.logging.v2.event.BaseEvent;

import java.time.format.DateTimeFormatter;

public class DPLogger {

    private static LoggerConfig CONFIG = null;

    private static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(ISO8601_FORMAT);

    private DPLogger() {
        // contains only static only methods - hide constructor.
    }

    public static void init(LoggerConfig loggerConfig) throws LoggingException {
        if (CONFIG == null) {
            synchronized (DPLogger.class) {
                if (CONFIG == null) {
                    CONFIG = loggerConfig;
                }
            }
        }
    }

    public static LoggerConfig logConfig() {
        if (CONFIG == null) {
            throw new LoggingException("DPLogger is not initalised");
        }
        return CONFIG;
    }

    public static <T extends BaseEvent> void log(T event) {
        CONFIG.getLogger().info(CONFIG.getSerialiser().toJson(event));
    }

    public static DateTimeFormatter formatter() {
        return FORMATTER;
    }
}

