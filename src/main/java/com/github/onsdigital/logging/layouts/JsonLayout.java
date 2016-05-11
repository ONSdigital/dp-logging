package com.github.onsdigital.logging.layouts;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;

/**
 * Custom layout to display log info in json format.
 */
public class JsonLayout extends LayoutBase<ILoggingEvent> {

    @Override
    public String doLayout(ILoggingEvent event) {
        return new JsonLogItem(event).asJson() + CoreConstants.LINE_SEPARATOR;
    }
}
