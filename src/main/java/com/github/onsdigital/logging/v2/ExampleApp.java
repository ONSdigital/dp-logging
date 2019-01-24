package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.serializer.JacksonEventSerialiser;
import org.slf4j.LoggerFactory;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.logInfo;
import static spark.Spark.get;

public class ExampleApp {

    static int iterations = 1;

    public static void main(String[] args) throws Exception {
        DPLogger.init(LoggerFactory.getLogger("com.test.app"), new JacksonEventSerialiser());

        get("/hello", (req, resp) -> {

            logInfo().requst(req.raw())
                    .log("GET: /hello request receieved ");

            resp.status(200);

            logInfo().requst(req.raw())
                    .response(resp.raw())
                    .log("GET: /hello request completed successfully");
            return "Hello!";
        });
    }

}
