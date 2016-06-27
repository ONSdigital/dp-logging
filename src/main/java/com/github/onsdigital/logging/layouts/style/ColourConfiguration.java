package com.github.onsdigital.logging.layouts.style;

import ch.qos.logback.classic.Level;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * Created by dave on 6/17/16.
 */
public class ColourConfiguration {

    protected static final String DEFAULT_COLOUR = "39m";
    private static final String PRIMARY_COLOUR_KEY = "{0}.primary.colour";
    private static final String SECONDARY_COLOUR_KEY = "{0}.secondary.colour";
    private static final String TERTIARY_COLOUR_KEY = "tertiary.colour";
    private static final String QUATERNARY_COLOUR_KEY = "quaternary.colour";
    private static final String COLOUR_LOGGING_ENABLED_KEY = "colour_logging_enabled";
    private static final String LOGGING_COLOUR_CONFIG_PATH = "/colour_logging_configuration.properties";

    private static Boolean colourLoggingEnabled = null;
    private static Properties colourLoggingProperties = null;

    private static String SECONDARY_COLOUR;
    private static String TERTIARY_COLOUR;
    private static String QUATERNARY_COLOUR;

    final String primaryColour;
    final String secondaryColour;

    public static void loadColours() {
        if (colourLoggingEnabled && colourLoggingProperties == null) {
            try (InputStream inputStream = ColourConfiguration.class
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

    public ColourConfiguration(Level level) {
        if (colourLoggingEnabled == null) {
            colourLoggingEnabled = Boolean.valueOf(System.getProperty(COLOUR_LOGGING_ENABLED_KEY, Boolean.FALSE.toString()));
        }

        if (colourLoggingEnabled) {
            loadColours();
            this.primaryColour = colourLoggingProperties.getProperty(MessageFormat.format(PRIMARY_COLOUR_KEY,
                    level.levelStr.toLowerCase()), DEFAULT_COLOUR);

            this.secondaryColour = colourLoggingProperties.getProperty(MessageFormat.format(SECONDARY_COLOUR_KEY,
                    level.levelStr.toLowerCase()), DEFAULT_COLOUR);
        } else {
            this.primaryColour = DEFAULT_COLOUR;
            this.secondaryColour = DEFAULT_COLOUR;
        }
    }

    public String getPrimaryColour() {
        return primaryColour;
    }

    public String getSecondaryColour() {
        return secondaryColour;
    }

    public String getTertiaryColour() {
        return TERTIARY_COLOUR;
    }

    public String getQuaternaryColour() {
        return QUATERNARY_COLOUR;
    }

    public static Boolean getColourLoggingEnabled() {
        return colourLoggingEnabled;
    }
}
