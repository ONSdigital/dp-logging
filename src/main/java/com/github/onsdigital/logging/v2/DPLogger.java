package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.config.LoggerConfig;
import com.github.onsdigital.logging.v2.event.BaseEvent;

public class DPLogger {

    private static LoggerConfig CONFIG = null;

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
}

