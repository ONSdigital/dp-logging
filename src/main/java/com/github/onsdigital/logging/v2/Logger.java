package com.github.onsdigital.logging.v2;

public interface Logger {

    void error(String event);

    void warn(String event);

    void info(String event);

    void debug(String event);

    String getName();
}
