package com.github.onsdigital.logging.v2.serializer;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.github.onsdigital.logging.v2.DPLogger;

public class NamingStrategy extends PropertyNamingStrategy {

    @Override
    public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
        if (defaultName == "data") {
            return DPLogger.logConfig().getNopDataNamespace();
        }
        return super.nameForField(config, field, defaultName);
    }
}
