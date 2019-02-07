package com.github.onsdigital.logging.v2.config;

import com.github.onsdigital.logging.v2.Logger;
import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.config.nop.LogConfig;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import com.github.onsdigital.logging.v2.storage.LogStore;

public class Builder {

    private Logger logger;
    private LogSerialiser serialiser;
    private String dataNamespace = "data";
    private ShutdownHook shutdownHook;
    private LogStore logStore;

    public Builder logger(Logger logger) {
        this.logger = logger;
        return this;
    }

    public Builder serialiser(LogSerialiser serialiser) {
        this.serialiser = serialiser;
        return this;
    }

    public Builder dataNamespace(String dataNamespace) {
        this.dataNamespace = dataNamespace;
        return this;
    }

    public Builder shutdownHook(ShutdownHook shutdownHook) {
        this.shutdownHook = shutdownHook;
        return this;
    }

    public Builder logStore(LogStore logStore) {
        this.logStore = logStore;
        return this;
    }

    public LogConfig create() throws LoggingException {
        return new Config(logger, serialiser, logStore, dataNamespace, shutdownHook);
    }
}
