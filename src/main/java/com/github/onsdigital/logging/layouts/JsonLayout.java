package com.github.onsdigital.logging.layouts;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import com.github.onsdigital.logging.layouts.model.JsonLogItem;
import com.github.onsdigital.logging.layouts.style.Styler;

import java.io.IOException;

/**
 * Custom layout to display log info in json format.
 */
public class JsonLayout extends LayoutBase<ILoggingEvent> {

    @Override
    public String doLayout(ILoggingEvent event) {
        try {
            return Styler.toJson(new JsonLogItem(event));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
