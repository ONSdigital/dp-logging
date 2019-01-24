package com.github.onsdigital.logging.v2.event;

import com.github.onsdigital.logging.v2.serializer.JacksonEventSerializer;

import java.time.ZonedDateTime;

public class Tester {

    public static void main(String[] args) throws Exception {

        ZonedDateTime start = ZonedDateTime.now();
        Thread.sleep(1000);

        SimpleEvent e = new SimpleEvent("test.app", Severity.INFO)
                .httpMethod("GET")
                .httpPath("/")
                .httpQuery("?name=dave")
                .httpScheme("https")
                .httpHost("localhost")
                .httpPort(8080)
                .httpStatusCode(200)
                .httpStartedAt(start)
                .httpEndedAt(ZonedDateTime.now())
                .httpDuration();

        System.out.println(new JacksonEventSerializer().toJson(e));
    }
}
