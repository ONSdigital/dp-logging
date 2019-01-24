package com.github.onsdigital.logging.v2.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.onsdigital.logging.v2.event.BaseEvent;

import java.time.ZonedDateTime;

public class JacksonEventSerializer implements EventSerializer {

    private ObjectMapper mapper;

    public JacksonEventSerializer() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        SimpleModule module = new SimpleModule();
        module.addSerializer(ZonedDateTime.class, new JacksonZonedlDateTimeSerializer());
        this.mapper.registerModule(module);

        this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public <T extends BaseEvent> String toJson(T event) {
        try {
            return mapper.writeValueAsString(event) + "\n";
        } catch (Exception e) {
            // TODO what do we do here?
            e.printStackTrace();
        }
        return "";
    }
}
