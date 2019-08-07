package com.github.onsdigital.logging.v2.config;

@FunctionalInterface
public interface ErrorWriter {

    boolean write(String s);
}
