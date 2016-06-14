package com.github.onsdigital.logging.layouts;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import com.github.onsdigital.logging.layouts.model.JsonLogItem;
import com.github.onsdigital.logging.layouts.model.PrettyJsonLogItem;

/**
 * Created by dave on 5/16/16.
 */
public class PrettyJsonLayout extends JsonLayout {

    @Override
    public String doLayout(ILoggingEvent event) {
        return new PrettyJsonLogItem(event).asJson();
    }
}
