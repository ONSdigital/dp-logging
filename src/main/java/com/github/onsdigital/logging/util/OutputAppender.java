package com.github.onsdigital.logging.util;

@Deprecated
@FunctionalInterface
public interface OutputAppender {

    void append(String key, String value);
}
