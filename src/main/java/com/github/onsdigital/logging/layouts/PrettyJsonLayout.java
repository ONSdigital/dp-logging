package com.github.onsdigital.logging.layouts;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.onsdigital.logging.layouts.model.JsonLogItem;
import com.github.onsdigital.logging.layouts.style.Styler;

import java.io.IOException;

/**
 * Created by dave on 5/16/16.
 */
public class PrettyJsonLayout extends JsonLayout {

    @Override
    public String doLayout(ILoggingEvent event) {
        try {
            return Styler.toPrettyJson(new JsonLogItem(event));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
