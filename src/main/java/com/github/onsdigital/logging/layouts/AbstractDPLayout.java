package com.github.onsdigital.logging.layouts;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.onsdigital.logging.layouts.model.JsonLogItem;

import java.io.IOException;

import static java.text.MessageFormat.format;

/**
 * Created by dave on 05/02/2018.
 */
@Deprecated
public abstract class AbstractDPLayout extends LayoutBase<ILoggingEvent> {

    private static final String DEBUG_COLOUR = "92;1m";
    private static final String INFO_COLOUR = "35;1m";
    private static final String WARN_COLOUR = "33;1m";
    private static final String ERROR_COLOUR = "31;1m";

    private static final String COLOUR_FORMAT = "\033[{0}{1}\033[0m";
    private static final String DP_COLOURED_LOGGING_ENV_KEY = "DP_COLOURED_LOGGING";
    private static boolean COLOUR_LOGGING_ENABLED;

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static ObjectWriter PRETTY_OBJECT_WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();

    static {
        COLOUR_LOGGING_ENABLED = Boolean.valueOf(System.getenv(DP_COLOURED_LOGGING_ENV_KEY));
    }

    /**
     * If the coloured logging is enabled the appropriate colour tags are pre/appended to the message.
     *
     * @param level   the level of the logging event.
     * @param message the logging message.
     * @return the message styled or unstyled depending on the configuration.
     */
    protected String addColour(String level, String message) {
        if (!COLOUR_LOGGING_ENABLED) {
            return message;
        }

        String colour = DEBUG_COLOUR;

        if (Level.DEBUG.levelStr.equals(level)) {
            colour = DEBUG_COLOUR;
        } else if (Level.INFO.levelStr.equals(level)) {
            colour = INFO_COLOUR;
        } else if (Level.WARN.levelStr.equals(level)) {
            colour = WARN_COLOUR;
        } else if (Level.ERROR.levelStr.equals(level)) {
            colour = ERROR_COLOUR;
        }
        return format(COLOUR_FORMAT, colour, message);
    }

    /**
     * Convert the message item to a JSON string.
     */
    protected String toJson(JsonLogItem item) throws IOException {
        return addColour(item.getLevel(), OBJECT_MAPPER.writeValueAsString(item)) + CoreConstants.LINE_SEPARATOR;
    }

    /**
     * Convert the message item to a formatted JSON string.
     */
    protected String toPrettyJson(JsonLogItem item) throws IOException {
        return addColour(item.getLevel(), PRETTY_OBJECT_WRITER.writeValueAsString(item)) + CoreConstants.LINE_SEPARATOR;
    }

    /**
     * Runs printStackTrace on an exception thrown in the doLayout method and re throws as a {@link RuntimeException}
     * to obey the {@link LayoutBase} interface.
     *
     * @param message a context message to indicate where the exception came from.
     * @param e       the cause.
     * @return a run time exception.
     */
    protected RuntimeException doLayoutException(String message, Throwable e) {
        RuntimeException runEx = new RuntimeException(message, e);
        runEx.printStackTrace();
        return runEx;
    }
}
