package com.github.onsdigital.logging.v2.config;

import ch.qos.logback.classic.LoggerContext;
import com.github.onsdigital.logging.v2.Logger;
import com.github.onsdigital.logging.v2.LoggerImpl;
import com.github.onsdigital.logging.v2.nop.NopConfig;
import com.github.onsdigital.logging.v2.serializer.JacksonLogSerialiser;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import com.github.onsdigital.logging.v2.storage.LogStore;
import com.github.onsdigital.logging.v2.storage.MDCLogStore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

public class DefaultConfig {

    private static final String DEFAULT_LOGGER_NAME_KEY = "default.logger.name";
    private static final String DEFAULT_LOGGER_NAME = "dp-logger-default";
    private static final String DEFAULT_LOGGER_FORMATTED_KEY = "default.logger.formatted";

    public static LogConfig get() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        String defaultName = loggerContext.getProperty(DEFAULT_LOGGER_NAME_KEY);
        if (StringUtils.isEmpty(defaultName)) {
            defaultName = DEFAULT_LOGGER_NAME;
        }

        Boolean formatted = Boolean.valueOf(loggerContext.getProperty(DEFAULT_LOGGER_FORMATTED_KEY));

        LogSerialiser serialiser = new JacksonLogSerialiser(formatted);
        LogStore store = new MDCLogStore(serialiser);
        Logger logger = new LoggerImpl(defaultName);
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
