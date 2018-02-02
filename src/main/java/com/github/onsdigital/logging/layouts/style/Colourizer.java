package com.github.onsdigital.logging.layouts.style;

import ch.qos.logback.classic.Level;

import java.io.InputStream;
import java.util.Properties;

import static java.lang.System.getenv;
import static java.text.MessageFormat.format;

/**
 * Apply colours to the logging output. To enable coloured logging set env var <i>colour_logging_enabled=true</i> by
 * default it is disabled.
 */
public class Colourizer {

    protected static final String DEFAULT_COLOUR = "39m";
    private static final String PRIMARY_COLOUR_KEY = "{0}.primary.colour";
    private static final String SECONDARY_COLOUR_KEY = "{0}.secondary.colour";
    private static final String TERTIARY_COLOUR_KEY = "tertiary.colour";
    private static final String QUATERNARY_COLOUR_KEY = "quaternary.colour";
    private static final String COLOUR_LOGGING_ENABLED_KEY = "colour_logging_enabled";
    private static final String LOGGING_COLOUR_CONFIG_PATH = "/colour_logging_configuration.properties";
    protected static final String START_TAG = "\033[";
    protected static final String END_TAG = "\033[0m";

    static final String APPLY_COLOUR_FMT = "{0}{1}{2}{3}";

    private static Boolean colourLoggingEnabled = null;
    private static Properties colourLoggingProperties = null;

    private static String SECONDARY_COLOUR;
    private static String TERTIARY_COLOUR;
    private static String QUATERNARY_COLOUR;

    private final String primaryColour;
    private final String secondaryColour;

    public static void loadColours() {
        if (colourLoggingEnabled && colourLoggingProperties == null) {
            try (InputStream inputStream = Colourizer.class
                    .getResourceAsStream(LOGGING_COLOUR_CONFIG_PATH)) {
                colourLoggingProperties = new Properties();
                colourLoggingProperties.load(inputStream);

                TERTIARY_COLOUR = colourLoggingProperties.getProperty(TERTIARY_COLOUR_KEY, DEFAULT_COLOUR);
                QUATERNARY_COLOUR = colourLoggingProperties.getProperty(QUATERNARY_COLOUR_KEY, DEFAULT_COLOUR);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public Colourizer(Level level) {
        if (colourLoggingEnabled == null) {
            colourLoggingEnabled = Boolean.valueOf(getenv(COLOUR_LOGGING_ENABLED_KEY));
        }

        if (colourLoggingEnabled) {
            loadColours();
            this.primaryColour = colourLoggingProperties.getProperty(format(PRIMARY_COLOUR_KEY,
                    level.levelStr.toLowerCase()), DEFAULT_COLOUR);

            this.secondaryColour = colourLoggingProperties.getProperty(format(SECONDARY_COLOUR_KEY,
                    level.levelStr.toLowerCase()), DEFAULT_COLOUR);
        } else {
            this.primaryColour = DEFAULT_COLOUR;
            this.secondaryColour = DEFAULT_COLOUR;
        }
    }

    public String primaryColour(String message) {
        if (colourLoggingEnabled) {
            return format(APPLY_COLOUR_FMT, START_TAG, primaryColour, message, END_TAG);
        }
        return message;
    }

    public String secondaryColour(String message) {
        if (colourLoggingEnabled) {
            return format(APPLY_COLOUR_FMT, START_TAG, secondaryColour, message, END_TAG);
        }
        return message;
    }

    public String tertiaryColour(String message) {
        if (colourLoggingEnabled) {
            return format(APPLY_COLOUR_FMT, START_TAG, TERTIARY_COLOUR, message, END_TAG);
        }
        return message;
    }

    public String quaternaryColour(String message) {
        if (colourLoggingEnabled) {
            return format(APPLY_COLOUR_FMT, START_TAG, QUATERNARY_COLOUR, message, END_TAG);
        }
        return message;
    }

    public static Boolean getColourLoggingEnabled() {
        return colourLoggingEnabled;
    }
}
