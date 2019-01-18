package com.github.onsdigital.logging.v2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mockito.Mockito;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;

import static com.github.onsdigital.logging.v2.Logger.logger;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.when;

public class Main {

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().registerTypeAdapter(HTTP.class, new HTTPAdapter()).setPrettyPrinting().create();
        Logger.init(gson, new PrintWriter(System.out), "zebedee.reader");

        MDC.put("trace_id", randomUUID().toString());

        logger().event("method and path")
                .httpMethod("POST")
                .httpPath("/a/b/c")
                .log();

        logger().event("just path")
                .httpPath("/a/b/c")
                .log();

        logger().event("just method")
                .httpMethod("POST")
                .log();

        logger().event("not http for me").log();

        logger().httpReq(mockReq())
                .event("logging method and path using just the requets")
                .log();
    }

    private static HttpServletRequest mockReq() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("ons/cmd/datasets");
        when(request.getQueryString()).thenReturn("?json=true");
        when(request.getScheme()).thenReturn("https");
        return request;
    }
}
