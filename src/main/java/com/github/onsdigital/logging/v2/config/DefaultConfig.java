package com.github.onsdigital.logging.v2.config;

import com.github.onsdigital.logging.v2.Logger;
import com.github.onsdigital.logging.v2.LoggerImpl;
import com.github.onsdigital.logging.v2.nop.NopConfig;
import com.github.onsdigital.logging.v2.serializer.JacksonLogSerialiser;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import com.github.onsdigital.logging.v2.storage.LogStore;
import com.github.onsdigital.logging.v2.storage.MDCLogStore;

public class DefaultConfig {

    public static LogConfig get() {
        LogSerialiser serialiser = new JacksonLogSerialiser();
        LogStore store = new MDCLogStore(serialiser);
        Logger logger = new LoggerImpl("dp-logger-default");
        try {
            return new Builder()
                    .serialiser(serialiser)
                    .logStore(store)
                    .logger(logger)
                    .create();
        } catch (Exception e) {
            System.out.println("failed to get default config, NOP will be used instead");
            e.printStackTrace();
            return new NopConfig();
        }
    }
}
