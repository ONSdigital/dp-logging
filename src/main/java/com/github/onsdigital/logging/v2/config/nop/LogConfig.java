package com.github.onsdigital.logging.v2.config.nop;

import com.github.onsdigital.logging.v2.Logger;
import com.github.onsdigital.logging.v2.config.ShutdownHook;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import com.github.onsdigital.logging.v2.storage.LogStore;

public interface LogConfig {

    Logger getLogger();

    LogSerialiser getSerialiser();

    String getNopNamespace();

    String getNopDataNamespace();

    ShutdownHook getShutdownHook();

    LogStore getLogStore();
}
