package com.github.onsdigital.logging.layouts.style;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.onsdigital.logging.builder.LogParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class Beautifier {

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    protected static final String START_TAG = "\033[";
    protected static final String END_TAG = "\033[0m";
    protected static final String PARAMS_KEY = "parameters={";

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

    public static String namespace(ILoggingEvent e, String logMessage) {
        return getColourConfig(e.getLevel()).quaternaryColour(logMessage);
    }

    public static String styleLogLevel(ILoggingEvent e) {
        return getColourConfig(e.getLevel()).primaryColour(e.getLevel().levelStr);
    }

    public static String styleThreadName(ILoggingEvent e) {
        return getColourConfig(e.getLevel()).quaternaryColour("[" + e.getThreadName() + "]");
    }

    public static String styleLoggerName(ILoggingEvent e) {
        return getColourConfig(e.getLevel()).quaternaryColour(e.getLoggerName());
    }

    public static String styleDate(ILoggingEvent e) {
        return getColourConfig(e.getLevel()).primaryColour(DATE_FORMAT.format(new Date()));
    }

    public static String styleMessage(ILoggingEvent e) {
        return getColourConfig(e.getLevel()).primaryColour(": " + e.getFormattedMessage() + " |");
    }

    public static String styleKeyValue(ILoggingEvent e, String key, String value) {
        ColourConfiguration cc = getColourConfig(e.getLevel());
        return cc.primaryColour(key) + cc.tertiaryColour("=") + cc.secondaryColour(value);
    }

    private static ColourConfiguration getColourConfig(Level level) {
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

    public static String beautifyParameters(ILoggingEvent e, LogParameters p) {
        if (p == null || p.getParameters() == null || p.getParameters().isEmpty()) {
            return "";
        }

        ColourConfiguration cc = getColourConfig(e.getLevel());
        StringBuilder result = new StringBuilder(cc.primaryColour(PARAMS_KEY));

        Iterator<Map.Entry<String, Object>> iterator = p.getParameters().entrySet().iterator();
        Map.Entry<String, Object> entry = null;
        while (iterator.hasNext()) {
            entry = iterator.next();
            result.append(cc.secondaryColour(entry.getKey() + "="))
                    .append(cc.tertiaryColour(entry.getValue().toString()))
                    .append(iterator.hasNext() ? cc.primaryColour(", ") : "");
        }
        result.append(cc.primaryColour("}"));
        return result.toString();
    }

    public static String beautifyJson(ILoggingEvent e, String json) {
        return getColourConfig(e.getLevel()).primaryColour(json);
    }
}
