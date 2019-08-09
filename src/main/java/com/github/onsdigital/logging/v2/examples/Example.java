package com.github.onsdigital.logging.v2.examples;

import com.github.onsdigital.logging.v2.DPLogger;
import com.github.onsdigital.logging.v2.Logger;
import com.github.onsdigital.logging.v2.LoggerImpl;
import com.github.onsdigital.logging.v2.config.Builder;
import com.github.onsdigital.logging.v2.config.LogConfig;
import com.github.onsdigital.logging.v2.config.ShutdownHook;
import com.github.onsdigital.logging.v2.nop.NopShutdownHook;
import com.github.onsdigital.logging.v2.serializer.JacksonLogSerialiser;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import com.github.onsdigital.logging.v2.storage.LogStore;
import com.github.onsdigital.logging.v2.storage.MDCLogStore;
import org.slf4j.LoggerFactory;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.warn;

public class Example {

    public static void main(String[] args) {
        try {
            LogSerialiser serialiser = new JacksonLogSerialiser(true);
            Logger logger = new LoggerImpl("logging-example");
            LogStore logStore = new MDCLogStore(serialiser);
            ShutdownHook shutdownHook = new NopShutdownHook();

            LogConfig config = new Builder()
                    .serialiser(serialiser)
                    .logger(logger)
                    .logStore(logStore)
                    .shutdownHook(shutdownHook)
                    .dataNamespace("logging-example-data")
                    .create();

            DPLogger.init(config);
        } catch (Exception ex) {
            // Handle exception
            System.exit(1);
        }

        info().traceID("1234")
                .serviceIdentity("batman")
                .data("key", "value")
                .log("info logging");

        warn().data("key", "value")
                .log("warn logging");

        error().data("key", "value")
                .log("error logging");

        error().traceID("1234")
                .serviceIdentity("batman")
                .data("key", "value")
                .exception(new RuntimeException("example exception"))
                .log("error logging with exception");

        CustomEvent.info().myField("batman").traceID("1234").log("customized event");

        ThirdPartyLibraryExample.doSomething();
    }

    static LogConfig getLogConfig() throws Exception {
        LogSerialiser serialiser = new JacksonLogSerialiser(true);
        Logger logger = new LoggerImpl("logging-example");
        LogStore logStore = new MDCLogStore(serialiser);
        ShutdownHook shutdownHook = new NopShutdownHook();

        return new Builder()
                .serialiser(serialiser)
                .logger(logger)
                .logStore(logStore)
                .shutdownHook(shutdownHook)
                .dataNamespace("logging-example-data")
                .create();
    }

    static class ThirdPartyLibraryExample {
        static org.slf4j.Logger logger = LoggerFactory.getLogger("third-party-library-example");

        static void doSomething() {
            try {
                throw new RuntimeException("something went wrong");
            } catch (Exception ex) {
                logger.error("doSomething encountered an unexpected error", ex);
            }
        }
    }
}
