package com.github.onsdigital.logging.layouts.model;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import com.google.gson.GsonBuilder;

/**
 * Created by dave on 5/16/16.
 */
public class PrettyJsonLogItem extends JsonLogItem {

    public PrettyJsonLogItem(ILoggingEvent event) {
        super(event);
    }

    @Override
    public String asJson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(this)
                + CoreConstants.LINE_SEPARATOR;
    }
}
