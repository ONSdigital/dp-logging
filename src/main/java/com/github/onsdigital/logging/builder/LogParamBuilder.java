package com.github.onsdigital.logging.builder;

/**
 * Created by dave on 5/5/16.
 */
@FunctionalInterface
public interface LogParamBuilder {

    LogParamBuilder addParameter(String key, Object value);
}
