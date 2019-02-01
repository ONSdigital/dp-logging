package com.github.onsdigital.logging.v2.config;

import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class LoggerConfig {

    private final Logger logger;
    private final LogSerialiser serialiser;
    private final String namespace;
    private String dataNamespace = "data";

    public LoggerConfig(Logger logger, LogSerialiser serialiser, String dataNamespace) {
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

        if (StringUtils.isNotEmpty(dataNamespace)) {
            this.dataNamespace = dataNamespace;
        }
    }

    public Logger getLogger() {
        return this.logger;
    }

    public LogSerialiser getSerialiser() {
        return this.serialiser;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getDataNamespace() {
        return this.dataNamespace;
    }
}
