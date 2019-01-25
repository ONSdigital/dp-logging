package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.serializer.JacksonEventSerialiser;
import com.github.onsdigital.logging.v2.time.LogEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.fatal;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.warn;
import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;

public class ExampleApp {

    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger("com.test.app");
        LoggerConfig loggerConfig = new LoggerConfig(logger, new JacksonEventSerialiser());
        DPLogger.init(loggerConfig);

        before((req, resp) -> {
            LogEventUtil.setTraceID(req.raw());
            info().beginHTTP(req.raw()).log("request received");
        });

        after((req, resp) -> info()
                .endHTTP(req.raw(), resp.raw())
                .log("request completed"));

        get("/hello", (req, resp) -> {
            Thread.sleep(5000);

            info().log("This is info");
            warn().log("This is warn");
            error().log("This is error");
            fatal().log("This is fatal");

            resp.status(200);
            return "Hello!";
        });
    }

}
