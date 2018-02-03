package com.github.onsdigital.logging.layouts.style;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.CoreConstants;
import com.github.onsdigital.logging.layouts.model.JsonLogItem;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import java.io.IOException;

import static java.text.MessageFormat.format;

/**
 * Created by dave on 03/02/2018.
 */
public class Styler {

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
    public static String addColour(String level, String message) {
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
    public static String toJson(JsonLogItem item) throws IOException {
        return addColour(item.getLevel(), OBJECT_MAPPER.writeValueAsString(item)) + CoreConstants.LINE_SEPARATOR;
    }

    /**
     * Convert the message item to a formatted JSON string.
     */
    public static String toPrettyJson(JsonLogItem item) throws IOException {
        return addColour(item.getLevel(), PRETTY_OBJECT_WRITER.writeValueAsString(item)) + CoreConstants.LINE_SEPARATOR;
    }
}
