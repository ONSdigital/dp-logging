package com.github.onsdigital.logging.v2.event;

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
    INFO(3);

    private final int level;

    private Severity(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }
}
