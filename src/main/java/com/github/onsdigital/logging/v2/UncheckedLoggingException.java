package com.github.onsdigital.logging.v2;

public class UncheckedLoggingException extends RuntimeException {

    public UncheckedLoggingException(String message) {
        super(message);
    }

    public UncheckedLoggingException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncheckedLoggingException(Throwable cause) {
        super(cause);
    }

    public UncheckedLoggingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
