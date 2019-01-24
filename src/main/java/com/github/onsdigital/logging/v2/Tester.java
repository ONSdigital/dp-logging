package com.github.onsdigital.logging.v2;

import com.github.onsdigital.logging.v2.event.Severity;
import com.github.onsdigital.logging.v2.event.SimpleEvent;
import com.github.onsdigital.logging.v2.serializer.JacksonEventSerialiser;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Tester {

    static int iterations = 1;

    public static void main(String[] args) throws Exception {
        DPLogger.init(LoggerFactory.getLogger("com.test.app"), new JacksonEventSerialiser());
        HttpServletRequest request = mockedRequest();

        new SimpleEvent("com.test.app", Severity.INFO)
                .requst(request)
                .httpMethod("GET")
                .authIdenity("test@test.gov.uk")
                .authIdentityTypeUser()
                .log("example log event");
    }

    static HttpServletRequest mockedRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/ons/aboutus/contactUs");
        when(request.getQueryString()).thenReturn("?help=true");
        when(request.getScheme()).thenReturn("https");
        return request;
    }
}
