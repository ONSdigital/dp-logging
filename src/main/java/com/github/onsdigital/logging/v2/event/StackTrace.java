package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;

public class StackTrace {

    private String file;
    private String function;

    @JsonInclude(JsonInclude.Include.ALWAYS)
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        StackTrace that = (StackTrace) o;

        return new EqualsBuilder()
                .append(getLine(), that.getLine())
                .append(getFile(), that.getFile())
                .append(getFunction(), that.getFunction())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getFile())
                .append(getFunction())
                .append(getLine())
                .toHashCode();
    }

    public static StackTrace[] stackTraceArrayFromThrowable(Throwable t) {
        if (t == null)
            return null;

        return Arrays.asList(t.getStackTrace())
                .stream()
                .map(e -> new StackTrace(e))
                .toArray(size -> new StackTrace[size]);
    }
}
