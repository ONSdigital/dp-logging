package com.github.onsdigital.logging.v2.example;

import com.github.onsdigital.logging.v2.DPLogger;
import com.github.onsdigital.logging.v2.config.LoggerConfig;
import com.github.onsdigital.logging.v2.serializer.JacksonEventSerialiser;
import com.github.onsdigital.logging.v2.time.LogEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;
import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;

public class SparkAPI {

    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger("com.test.app");
        LoggerConfig loggerConfig = new LoggerConfig(logger, new JacksonEventSerialiser(), "simple_app_data");
        DPLogger.init(loggerConfig);

        before((req, resp) -> {
            LogEventUtil.setTraceID(req.raw());
            info().beginHTTP(req.raw()).log("request received");
        });

        after((req, resp) -> info()
                .endHTTP(req.raw(), resp.raw())
                .log("request completed"));

        get("/hello", (req, resp) -> {

            info().data("key1", "value1")
                    .data("key2", "value2")
                    .log("doing something....");

            resp.status(200);
            return "Hello!";
        });

        get("/break", (req, resp) -> {
            List<String> l = null;

            try {
                l.stream().forEach(s -> System.out.println(s));
            } catch (NullPointerException e) {
                IOException ioException = new IOException("Wrapped Exception in another exception", e);
                throw error().logAndThrow(ioException, "failed to do something...");
            }
            resp.status(200);
            return "done";
        });
    }

}
