package com.github.onsdigital.logging.v2.event;

import ch.qos.logback.classic.Level;

public enum Severity {

    /**
     * A catastrophic failure event resulting in the death of the application... cancel your evening a weekend plans.
     */
    FATAL(0),

    /**
     * An unrecoverable failure event that halts the current flow of execution.
     */
    ERROR(1),

    /**
     * A failure event that can be managed within the current flow of execution (e.g. retried)
     */
    WARN(2),

    /**
     * All non-failure events
     */
    INFO(3),


    DEBUG(4);

    private final int level;

    private Severity(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public static Severity getSeverity(int level) {
        switch (level) {
            case 0:
                return FATAL;
            case 1:
                return ERROR;
            case 2:
                return WARN;
            case 3:
                return INFO;
            default:
                return DEBUG;
        }
    }

    public static Severity getSeverity(Level level) {
        if (level.toInteger() == Level.ERROR_INT)
            return ERROR;

        if (level.toInteger() == Level.WARN_INT)
            return WARN;

        return INFO;
    }
}
