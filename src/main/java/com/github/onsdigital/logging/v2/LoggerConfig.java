package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.serializer.EventSerialiser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class LoggerConfig {

    private final Logger logger;
    private final EventSerialiser serialiser;
    private final String namespace;

    public LoggerConfig(Logger logger, EventSerialiser serialiser) {
        if (logger == null) {
            throw new LoggingException("DPLogger failed to initialise: Logger was null");
        }
        this.logger = logger;

        if (serialiser == null) {
            throw new LoggingException("DPLogger failed to initialise: EventSerialiser was null");
        }
        this.serialiser = serialiser;

        if (StringUtils.isEmpty(logger.getName())) {
            throw new LoggingException("DPLogger failed initialise: namespace was null or empty");
        }
        this.namespace = logger.getName();
    }

    public Logger getLogger() {
        return this.logger;
    }

    public EventSerialiser getSerialiser() {
        return this.serialiser;
    }

    public String getNamespace() {
        return this.namespace;
    }
}
