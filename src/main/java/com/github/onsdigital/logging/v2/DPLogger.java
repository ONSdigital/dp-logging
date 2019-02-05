package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.config.Config;
import com.github.onsdigital.logging.v2.event.BaseEvent;
import com.github.onsdigital.logging.v2.event.SimpleEvent;
import org.slf4j.Logger;

import java.time.format.DateTimeFormatter;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static java.text.MessageFormat.format;

public class DPLogger {

    private static Config CONFIG = null;

    private static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    static final String MARSHAL_FAILURE = "failed to marshal log event to json: event {0}, exception: {1}";
    static final String MARSHALL_EVENT_ERR = "error marshalling dp log event to json";

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

    static void reload(Config config) {
        synchronized (DPLogger.class) {
            CONFIG = config;
        }
    }

    public static Config logConfig() {
        if (CONFIG == null) {
            throw new UncheckedLoggingException("DPLogger is not initalised");
        }
        return CONFIG;
    }

    public static <T extends BaseEvent> void log(T event) {
        Logger logger = CONFIG.getLogger();
        try {
            logger.info(marshal(event, true));
        } catch (LoggingException ex) {
            System.out.println(format(MARSHAL_FAILURE, event, ex));
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
                // try marshalling an error event before giving up.
                SimpleEvent err = error(MARSHALL_EVENT_ERR)
                        .exception(ex)
                        .data("event", event.toString());
                return marshal(err, false);
            }
            throw ex;
        }
    }

    public static DateTimeFormatter formatter() {
        return FORMATTER;
    }
}

