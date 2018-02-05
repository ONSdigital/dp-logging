package com.github.onsdigital.logging.layouts;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.onsdigital.logging.layouts.model.JsonLogItem;

import java.io.IOException;

/**
 * Custom layout to display log info in json format.
 */
public class JsonLayout extends AbstractDPLayout {

    @Override
    public String doLayout(ILoggingEvent event) {
        try {
            return toJson(new JsonLogItem(event));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
