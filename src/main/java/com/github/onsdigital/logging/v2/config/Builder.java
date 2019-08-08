package com.github.onsdigital.logging.v2.config;

import com.github.onsdigital.logging.v2.Logger;
import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import com.github.onsdigital.logging.v2.storage.LogStore;

public class Builder {

    private Logger logger;
    private LogSerialiser serialiser;
    private String dataNamespace = "data";
    private ShutdownHook shutdownHook;
    private LogStore logStore;
    private ErrorWriter errorWriter;

    public Builder() {
        this.errorWriter = new DefaultErrorWriter();
    }

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

    public Builder errorWriter(ErrorWriter errorWriter) {
        this.errorWriter = errorWriter;
        return this;
    }

    public LogConfig create() throws LoggingException {
        return new Config(logger, serialiser, logStore, dataNamespace, shutdownHook, errorWriter);
    }

    static class DefaultErrorWriter implements ErrorWriter {

        @Override
        public boolean write(String s) {
            System.out.println(s);
            return System.out.checkError();
        }
    }
}
