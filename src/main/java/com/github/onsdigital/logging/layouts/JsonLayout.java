package com.github.onsdigital.logging.layouts;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.onsdigital.logging.layouts.model.JsonLogItem;

import java.io.IOException;

/**
 * Custom layout to display log info in json format.
 */
@Deprecated
public class JsonLayout extends AbstractDPLayout {

    static final String DO_LAYOUT_ERR = "JsonLayout.doLayout returned an unexpected error";

    @Override
    public String doLayout(ILoggingEvent event) {
        try {
            return toJson(new JsonLogItem(event));
        } catch (IOException e) {
            throw doLayoutException(DO_LAYOUT_ERR, e);
        }
    }
}
