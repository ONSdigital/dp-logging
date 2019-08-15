package com.github.onsdigital.logging.v2.examples;

import com.github.onsdigital.logging.v2.event.BaseEvent;
import com.github.onsdigital.logging.v2.event.Severity;
import org.apache.commons.lang3.StringUtils;

import static com.github.onsdigital.logging.v2.DPLogger.logConfig;

public class CustomEvent extends BaseEvent<CustomEvent> {

    protected CustomEvent(Severity severity) {
        super(logConfig().getNamespace(), severity, logConfig().getLogStore());
    }

    public static CustomEvent warn() {
        return new CustomEvent(Severity.WARN);
    }

    public static CustomEvent info() {
        return new CustomEvent(Severity.INFO);
    }

    public static CustomEvent error() {
        return new CustomEvent(Severity.ERROR);
    }

    public CustomEvent myField(String value) {
        if (StringUtils.isNotEmpty(value))
            data("myField", value);
        return this;
    }
}
