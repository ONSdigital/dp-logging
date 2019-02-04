package com.github.onsdigital.logging.v2.config;

@FunctionalInterface
public interface ShutdownHook {

    void shutdown();
}
