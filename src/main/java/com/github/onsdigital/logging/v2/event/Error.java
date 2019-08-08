package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import static com.github.onsdigital.logging.v2.event.StackTrace.stackTraceArrayFromThrowable;

public class Error {

    private String message;

    @JsonProperty("stack_trace")
    private StackTrace[] stackTraces;

    private Map<String, Object> data;

    public Error(Throwable t) {
        this.message = t.getClass().getName() + ": " + t.getMessage();
        this.stackTraces = stackTraceArrayFromThrowable(t);
        this.data = null;
    }

    public String getMessage() {
        return this.message;
    }

    public StackTrace[] getStackTraces() {
        return this.stackTraces;
    }

    public Map<String, Object> getData() {
        return this.data;
    }
}
