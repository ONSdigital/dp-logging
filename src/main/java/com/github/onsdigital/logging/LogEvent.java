package com.github.onsdigital.logging;

/**
 * Type for each of
 */
public enum LogEvent implements Loggable{

    LOGIN_SUCCESS("Login success"),
    LOGIN_AUTH_FAILURE("Login authentication failure"),
    PASSWORD_CHANGE_REQUIRED("Login password change required.");

    private final LogBuilder builder;
    private final String name;

    /**
     * Create a LogEvent enum.
     *
     * @param name a human readable name of the logging event.
     */
    LogEvent(String name) {
        this.name = name;
        this.builder = new LogBuilder(this);
    }

    public LogBuilder getBuilder() {
        return builder;
    }

    public String getName() {
        return name;
    }
}
