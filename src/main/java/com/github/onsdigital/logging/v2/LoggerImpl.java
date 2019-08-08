package com.github.onsdigital.logging.v2;

import org.slf4j.LoggerFactory;

public class LoggerImpl implements Logger {

    private org.slf4j.Logger log;

    public LoggerImpl(String name) {
        this.log = LoggerFactory.getLogger(name);
    }

    @Override
    public void error(String event) {
        log.error(event);
    }

    @Override
    public void warn(String event) {
        log.warn(event);
    }

    @Override
    public void info(String event) {
        this.log.info(event);
    }

    @Override
    public String getName() {
        return this.log.getName();
    }
}
