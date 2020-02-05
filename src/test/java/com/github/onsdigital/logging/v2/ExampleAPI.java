package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.config.Builder;
import com.github.onsdigital.logging.v2.serializer.JacksonLogSerialiser;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import com.github.onsdigital.logging.v2.storage.LogStore;
import com.github.onsdigital.logging.v2.storage.MDCLogStore;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.debug;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;
import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.port;

public class ExampleAPI {

    static {
        System.setProperty("logback.configurationFile", ExampleAPI.class.getResource("/logback.xml").getPath());
    }

    public static void main(String[] args) throws Exception {
        setUpLogging();
        port(8088);

        before("/*", (req, resp) -> {
            debug().log("DEBUG request in");
            info().request(req.raw()).log("request receieved");
        });

        after("/*", ((request, response) -> {
            info().request(request.raw()).response(response.raw()).log("request processing compelete");
        }));

        get("/hello", (req, resp) -> {
            info().log("handling hello request");
            return "hello world";
        });

    }

    static void setUpLogging() throws Exception {
        LogSerialiser serialiser = new JacksonLogSerialiser(true);
        LogStore store = new MDCLogStore(serialiser);
        Logger logger = new LoggerImpl("logging-example");

        DPLogger.init(new Builder()
                .logger(logger)
                .logStore(store)
                .serialiser(serialiser)
                .dataNamespace("data")
                .create());
    }
}
