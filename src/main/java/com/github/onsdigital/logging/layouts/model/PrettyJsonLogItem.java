package com.github.onsdigital.logging.layouts.model;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import com.google.gson.GsonBuilder;

/**
 * Created by dave on 5/16/16.
 */
public class PrettyJsonLogItem extends JsonLogItem {

    protected static final String START_TAG = "\033[";
    protected static final String END_TAG = "\033";
    protected static final String ERROR_RED = "31;1m";
    protected static final String DEBUG_GREEN = "32m";
    protected static final String WARN_YELLOW = "93m";
    protected static final String INFO_BLUE = "96m";

    protected transient String colour = null;

    public PrettyJsonLogItem(ILoggingEvent event) {
        super(event);

        if (event.getLevel().equals(Level.ERROR)) {
            colour = ERROR_RED;
        } else if (event.getLevel().equals(Level.WARN)) {
            colour = WARN_YELLOW;
        } else if (event.getLevel().equals(Level.INFO)) {
            colour = INFO_BLUE;
        } else {
            colour = DEBUG_GREEN;
        }
    }

    @Override
    public String asJson() {
        String json = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(this)
                + CoreConstants.LINE_SEPARATOR;
        if (this.colour != null) {
            json = START_TAG + this.colour + json + END_TAG;
        }
        return json;
    }
}
