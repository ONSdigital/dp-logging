package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.serializer.JacksonEventSerialiser;
import com.github.onsdigital.logging.v2.time.LogEventUtil;
import org.slf4j.LoggerFactory;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.logInfo;
import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;

public class ExampleApp {

    public static void main(String[] args) throws Exception {
        DPLogger.init(LoggerFactory.getLogger("com.test.app"), new JacksonEventSerialiser());

        before((req, resp) -> {
            LogEventUtil.setTraceID(req.raw());
            logInfo().beginHTTP(req.raw()).log("request received");
        });

        after((req, resp) -> logInfo()
                .endHTTP(req.raw(), resp.raw())
                .log("request completed"));

        get("/hello", (req, resp) -> {
            Thread.sleep(5000);
            resp.status(200);
            return "Hello!";
        });
    }

}
