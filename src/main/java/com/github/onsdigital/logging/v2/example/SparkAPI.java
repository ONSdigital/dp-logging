package com.github.onsdigital.logging.v2.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.logging.v2.DPLogger;
import com.github.onsdigital.logging.v2.config.Builder;
import com.github.onsdigital.logging.v2.config.Config;
import com.github.onsdigital.logging.v2.config.ShutdownHook;
import com.github.onsdigital.logging.v2.event.BaseEvent;
import com.github.onsdigital.logging.v2.event.Severity;
import com.github.onsdigital.logging.v2.serializer.JacksonLogSerialiser;
import org.slf4j.LoggerFactory;
import spark.Route;

import java.util.List;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.stop;

public class SparkAPI {

    public static void main(String[] args) throws Exception {
        System.setProperty("logback.configurationFile", "example-logback.xml");

        ShutdownHook shutdownHook = () -> {
            System.out.println("shutdown hook invoked");
            System.out.println("shutting down spark");
            stop();
            System.out.println("existing application...");
            System.exit(1);
        };

        Config config = new Builder()
                .logger(LoggerFactory.getLogger("com.test.app"))
                .serialiser(new JacksonLogSerialiser())
                .dataNamespace("simple_app_data")
                .shutdownHook(shutdownHook)
                .create();

        DPLogger.init(config);

        before((req, resp) -> info().beginHTTP(req.raw()).log("request received"));

        after((req, resp) -> info().endHTTP(resp.raw()).log("request completed"));

        get("/hello", (req, resp) -> {
            info().data("key1", "value1").data("key2", "value2").log("doing something....");
            new Erroring("error.test", Severity.INFO).log("this should error");
            resp.status(200);
            return "Hello!";
        });

        get("/break", getBreakRoute());
    }

    private static Route getBreakRoute() {
        return (req, resp) -> {
            List<String> l = null;

            try {
                if (l == null) {
                    throw new RuntimeException("Something broke...");
                }
            } catch (Exception e) {
                error().logAndThrow(e, "something went wrong");
            }
            resp.status(200);
            return "done";
        };
    }

    static class Erroring extends BaseEvent<Erroring> {

        public Erroring(String namespace, Severity severity) {
            super(namespace, severity);
        }
    }

}
