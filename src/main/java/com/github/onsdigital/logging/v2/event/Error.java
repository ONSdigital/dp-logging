package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Error error = (Error) o;

        return new EqualsBuilder()
                .append(getMessage(), error.getMessage())
                .append(getStackTraces(), error.getStackTraces())
                .append(getData(), error.getData())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getMessage())
                .append(getStackTraces())
                .append(getData())
                .toHashCode();
    }
}
