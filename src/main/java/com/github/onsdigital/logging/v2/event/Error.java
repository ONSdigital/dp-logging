package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Map;

public class Error {

    private String message;

    @JsonProperty("stack_trace")
    private StackTrace[] stackTraces;

    private Map<String, Object> data;

    public Error(Throwable t) {
        this.message = t.getClass().getName() + ": " + t.getMessage();
        this.stackTraces = Arrays.asList(t.getStackTrace())
                .stream()
                .map(e -> new StackTrace(e))
                .toArray(size -> new StackTrace[size]);
        this.data = null;
    }

    static class StackTrace {
        private String file;
        private String function;
        private int line;

        public StackTrace(StackTraceElement e) {
            this.line = e.getLineNumber();
            this.file = e.getClassName();
            this.function = e.getMethodName();
        }

        public int getLine() {
            return this.line;
        }

        public String getFile() {
            return this.file;
        }

        public String getFunction() {
            return this.function;
        }
    }
}
