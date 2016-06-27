package com.github.onsdigital.logging.layouts.style;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.onsdigital.logging.builder.LogParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import static com.github.onsdigital.logging.layouts.style.ColourConfiguration.getColourLoggingEnabled;

/**
 *
 */
public class Beautifier {

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    protected static final String START_TAG = "\033[";
    protected static final String END_TAG = "\033[0m";
    protected static final ColourConfiguration ERROR_CONFIG;
    protected static final ColourConfiguration WARN_CONFIG;
    protected static final ColourConfiguration INFO_CONFIG;
    protected static final ColourConfiguration DEBUG_CONFIG;
    protected static final ColourConfiguration TRACE_CONFIG;

    static {
        ERROR_CONFIG = new ColourConfiguration(Level.ERROR);
        WARN_CONFIG = new ColourConfiguration(Level.WARN);
        INFO_CONFIG = new ColourConfiguration(Level.INFO);
        DEBUG_CONFIG = new ColourConfiguration(Level.DEBUG);
        TRACE_CONFIG = new ColourConfiguration(Level.TRACE);
    }

    public static String tertiaryColour(ILoggingEvent event, String logMessage) {
        return beautify(getColourMapping(event.getLevel()).getQuaternaryColour(), logMessage);
    }

    public static String styleLogLevel(ILoggingEvent event) {
        return beautify(getColourMapping(event.getLevel()).getPrimaryColour(), event.getLevel().levelStr);
    }

    public static String styleThreadName(ILoggingEvent event) {
        return beautify(getColourMapping(event.getLevel()).getQuaternaryColour(), "[" + event.getThreadName() + "]");
    }

    public static String styleLoggerName(ILoggingEvent event) {
        return beautify(getColourMapping(event.getLevel()).getQuaternaryColour(), event.getLoggerName());
    }

    public static String styleDate(ILoggingEvent event) {
        return beautify(getColourMapping(event.getLevel()).getPrimaryColour(), DATE_FORMAT.format(new Date()));
    }

    public static String styleMessage(ILoggingEvent event) {
        return beautify(getColourMapping(event.getLevel()).getPrimaryColour(), ": " + event.getFormattedMessage() +
                " |");
    }

    public static String styleKeyValue(ILoggingEvent event, String key, String value) {
        return beautify(getColourMapping(event.getLevel()).getPrimaryColour(), key)
                + beautify(getColourMapping(event.getLevel()).getTertiaryColour(), "=")
                + beautify(getColourMapping(event.getLevel()).getSecondaryColour(), value);
    }

    public static String beautify(String colourCode, String logMessage) {
        if (!getColourLoggingEnabled()) {
            return logMessage;
        }
        return START_TAG + colourCode + logMessage + END_TAG;
    }

    private static ColourConfiguration getColourMapping(Level level) {
        if (Level.ERROR.equals(level)) {
            return ERROR_CONFIG;
        } else if (Level.WARN.equals(level)) {
            return WARN_CONFIG;
        } else if (Level.INFO.equals(level)) {
            return INFO_CONFIG;
        } else if (Level.DEBUG.equals(level)) {
            return DEBUG_CONFIG;
        } else {
            return TRACE_CONFIG;
        }
    }

    public static String beautifyParameters(ILoggingEvent event, LogParameters parameters) {
        if (parameters == null || parameters.getParameters() == null || parameters.getParameters().isEmpty()) {
            return "";
        }

        ColourConfiguration configuration = getColourMapping(event.getLevel());
        StringBuilder result = new StringBuilder(applyColour(configuration.getPrimaryColour(), "parameters={ "));

        Iterator<Map.Entry<String, Object>> iterator = parameters.getParameters().entrySet().iterator();
        Map.Entry<String, Object> entry = null;
        while (iterator.hasNext()) {
            entry = iterator.next();
            result.append(START_TAG)
                    .append(applyColour(configuration.getPrimaryColour(), entry.getKey() + "="))
                    .append(applyColour(configuration.getSecondaryColour(), entry.getValue().toString()))
                    .append(iterator.hasNext() ? applyColour(configuration.getSecondaryColour(), ", ") : "");
        }
        result.append(applyColour(configuration.getPrimaryColour(), " }"));
        return result.toString();
    }

    public static String beautifyJson(ILoggingEvent event, String json) {
        return beautify(getColourMapping(event.getLevel()).getPrimaryColour(), json);
    }

    private static String applyColour(String colour, String message) {
        return START_TAG + colour + message + END_TAG;
    }
}
