package com.github.onsdigital.logging.v2.config;

import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import org.slf4j.Logger;

public class Builder {

    private Logger logger;
    private LogSerialiser serialiser;
    private String dataNamespace = "data";
    private ShutdownHook shutdownHook;

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

    public Config create() throws LoggingException {
        return new Config(logger, serialiser, dataNamespace, shutdownHook);
    }
}
