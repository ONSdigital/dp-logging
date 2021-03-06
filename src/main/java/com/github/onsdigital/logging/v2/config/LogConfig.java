package com.github.onsdigital.logging.v2.config;

import com.github.onsdigital.logging.v2.Logger;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import com.github.onsdigital.logging.v2.storage.LogStore;

public interface LogConfig {

    Logger getLogger();

    LogSerialiser getSerialiser();

    String getNamespace();

    String getDataNamespace();

    ShutdownHook getShutdownHook();

    LogStore getLogStore();

    ErrorWriter getErrorWriter();
}
