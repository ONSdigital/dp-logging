package com.github.onsdigital.logging.util;

@FunctionalInterface
public interface OutputAppender {

    void append(String key, String value);
}
