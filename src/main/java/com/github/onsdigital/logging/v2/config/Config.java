package com.github.onsdigital.logging.v2.config;

import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import com.github.onsdigital.logging.v2.storage.LogStore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class Config {

    private Logger logger;
    private LogSerialiser serialiser;
    private String namespace;
    private String dataNamespace = "data";
    private ShutdownHook shutdownHook;
    private LogStore logStore;

    Config(Logger logger, LogSerialiser serialiser, LogStore logStore, String dataNamespace, ShutdownHook shutdownHook)
            throws LoggingException {
        if (logger == null) {
            throw new LoggingException("DPLogger failed to initialise: Logger was null");
        }
        this.logger = logger;

        if (serialiser == null) {
            throw new LoggingException("DPLogger failed to initialise: EventSerialiser was null");
        }
        this.serialiser = serialiser;

        if (logStore == null) {
            throw new LoggingException("DPLogger failed to initialise: LogStore was null");
        }
        this.logStore = logStore;

        if (StringUtils.isEmpty(logger.getName())) {
            throw new LoggingException("DPLogger failed initialise: namespace was null or empty");
        }
        this.namespace = logger.getName();

        if (StringUtils.isNotEmpty(dataNamespace)) {
            this.dataNamespace = dataNamespace;
        }

        this.shutdownHook = shutdownHook;
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

    public ShutdownHook getShutdownHook() {
        return this.shutdownHook;
    }

    public LogStore getLogStore() {
        return this.logStore;
    }
}
