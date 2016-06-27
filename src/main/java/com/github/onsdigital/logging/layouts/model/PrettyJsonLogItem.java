package com.github.onsdigital.logging.layouts.model;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

import static com.github.onsdigital.logging.layouts.style.Beautifier.beautifyJson;

/**
 * Created by dave on 5/16/16.
 */
public class PrettyJsonLogItem extends JsonLogItem {

    public PrettyJsonLogItem(ILoggingEvent event) {
        super(event);
    }

    @Override
    public String asJson() {
        try {
            return beautifyJson(this.event, new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this)
                    + CoreConstants.LINE_SEPARATOR);
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }
}
