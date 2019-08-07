package com.github.onsdigital.logging.v2.nop;

import com.github.onsdigital.logging.v2.config.ErrorWriter;
import com.github.onsdigital.logging.v2.Logger;
import com.github.onsdigital.logging.v2.config.LogConfig;
import com.github.onsdigital.logging.v2.config.ShutdownHook;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import com.github.onsdigital.logging.v2.storage.LogStore;

public class NopConfig implements LogConfig {

    private Logger nopLog;
    private LogSerialiser nopSerialiser;
    private LogStore nopStore;
    private String nopNamespace;
    private String nopDataNamespace;
    private ShutdownHook nopShutdownHook;
    private ErrorWriter nopErrorWriter;

    public NopConfig() {
        this.nopSerialiser = new NopLogSerialiser();
        this.nopStore = new NopStore();
        this.nopNamespace = "nop.namespace";
        this.nopDataNamespace = "nop.data.namespace";
        this.nopShutdownHook = new NopShutdownHook();
        this.nopLog = new NopLogger();
        this.nopErrorWriter = (s) -> false;
    }

    @Override
    public Logger getLogger() {
        return nopLog;
    }

    @Override
    public LogSerialiser getSerialiser() {
        return nopSerialiser;
    }

    @Override
    public String getNamespace() {
        return nopNamespace;
    }

    @Override
    public String getDataNamespace() {
        return nopDataNamespace;
    }

    @Override
    public ShutdownHook getShutdownHook() {
        return nopShutdownHook;
    }

    @Override
    public LogStore getLogStore() {
        return nopStore;
    }

    @Override
    public ErrorWriter getErrorWriter() {
        return nopErrorWriter;
    }
}
