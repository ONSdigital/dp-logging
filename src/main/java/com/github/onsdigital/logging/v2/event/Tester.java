package com.github.onsdigital.logging.v2.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public class Tester {

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        SimpleEvent e = new SimpleEvent("test.app", Severity.INFO)
                .httpMethod("GET")
                .httpPath("/")
                .httpQuery("?name=dave")
                .httpScheme("https")
                .httpHost("localhost")
                .httpPort(8080)
                .httpStatusCode(200)
                .httpStartedAt(LocalDateTime.now())
                .httpEndedAt(LocalDateTime.now())
                .httpDuration();

        System.out.println(gson.toJson(e));
    }
}
