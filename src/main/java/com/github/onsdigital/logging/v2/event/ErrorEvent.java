package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.exception.ExceptionUtils;

import static com.github.onsdigital.logging.v2.DPLogger.logConfig;

public class ErrorEvent extends BaseEvent<ErrorEvent> {

    @JsonProperty("stack_trace")
    private String[] stackTrace;

    @JsonProperty("error_cause")
    private String cause;

    protected transient Throwable throwable;

    protected ErrorEvent(String namespace, Severity severity, Throwable t) {
        super(namespace, severity);
        this.throwable = t;
        if (t != null) {
            this.cause = t.getMessage();
            this.stackTrace = ExceptionUtils.getStackFrames(t);
        }
    }

    @Override
    public void log(String event) {
        super.log(event);
    }

    public <T extends Throwable> T logAndThrow(T t, String event) {
        log(event);
        return t;
    }

    public static ErrorEvent error(Throwable t) {
        return new ErrorEvent(logConfig().getNamespace(), Severity.ERROR, t);
    }

    public static ErrorEvent fatal(Throwable t) {
        return new ErrorEvent(logConfig().getNamespace(), Severity.FATAL, t);
    }
}
