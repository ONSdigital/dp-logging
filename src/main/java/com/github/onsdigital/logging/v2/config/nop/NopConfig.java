package com.github.onsdigital.logging.v2.config.nop;

import com.github.onsdigital.logging.v2.Logger;
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

    public NopConfig() {
        this.nopSerialiser = null;
        this.nopStore = new NopStore();
        this.nopNamespace = "nop.namespace";
        this.nopDataNamespace = "nop.data.namespace";
        this.nopShutdownHook = new NopShutdownHook();
        this.nopLog = new NopLogger();
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
    public String getNopNamespace() {
        return nopNamespace;
    }

    @Override
    public String getNopDataNamespace() {
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
}
