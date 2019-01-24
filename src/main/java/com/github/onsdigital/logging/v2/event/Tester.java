package com.github.onsdigital.logging.v2.event;

import com.github.onsdigital.logging.v2.serializer.EventSerializer;
import com.github.onsdigital.logging.v2.serializer.JacksonEventSerializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Tester {

    static int iterations = 1000;

    public static void main(String[] args) throws Exception {

        long jacksonResult = withJackson();
        System.out.println("jackson serializer: " + jacksonResult);
    }

    static long withJackson() {
        EventSerializer jackson = new JacksonEventSerializer();
        LocalDateTime jacksonStart = LocalDateTime.now();

        for (int i = 0; i < iterations; i++) {
            System.out.println(jackson.toJson(newEvent()));
        }
        LocalDateTime jacksonEnd = LocalDateTime.now();
        return Duration.between(jacksonStart, jacksonEnd).toMillis();
    }

    static SimpleEvent newEvent() {
        return new SimpleEvent("test.app", Severity.INFO)
                .httpMethod("GET")
                .httpPath("/")
                .httpQuery("?name=dave")
                .httpScheme("https")
                .httpHost("localhost")
                .httpPort(8080)
                .httpStatusCode(200)
                .httpStartedAt(ZonedDateTime.now())
                .httpEndedAt(ZonedDateTime.now())
                .httpDuration();
    }
}
