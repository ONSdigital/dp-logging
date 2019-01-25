package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.serializer.JacksonEventSerialiser;
import com.github.onsdigital.logging.v2.time.LogEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;
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

            Thread.sleep(1000);
            info().log("thinking about it...");

            Thread.sleep(1000);
            info().log("still thinking about it...");

            Thread.sleep(1000);
            info().log("almost finished...");

            Thread.sleep(1000);

            resp.status(200);
            return "Hello!";
        });
    }

}
