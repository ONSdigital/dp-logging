package com.github.onsdigital.logging.v2.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.event.BaseEvent;
import com.github.onsdigital.logging.v2.event.HTTP;
import com.github.onsdigital.logging.v2.event.SimpleEvent;

import java.time.ZonedDateTime;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;

public class JacksonLogSerialiser implements LogSerialiser {

    private ObjectMapper mapper;

    public JacksonLogSerialiser() {
        this(false);
    }

    public JacksonLogSerialiser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JacksonLogSerialiser(boolean isPretty) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(ZonedDateTime.class, new JacksonZonedlDateTimeSerialiser());
        module.addDeserializer(ZonedDateTime.class, new JacksonZonedlDateTimeDeserialiser());

        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.registerModule(module);
        this.mapper.setPropertyNamingStrategy(new NamingStrategy());
        this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        if (isPretty) {
            this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
    }

    @Override
    public <T extends BaseEvent> String toJson(T event) throws LoggingException {
        try {
            return mapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new LoggingException("error marshalling event to json", e);
        }
    }

    @Override
    public <T extends BaseEvent> String toJsonRetriable(T event) throws LoggingException {
        try {
            return toJson(event);
        } catch (Exception ex) {
            SimpleEvent err = error("failed to marshal log event to json")
                    .exception(ex)
                    .data("event", event.toString());

            return toJson(err);
        }
    }

    @Override
    public String toJson(HTTP http) {
        try {
            return mapper.writeValueAsString(http);
        } catch (Exception e) {
            // TODO what do we do here?
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public HTTP getHTTP(String json) {
        try {
            return mapper.readValue(json, HTTP.class);
        } catch (Exception e) {
            // TODO what to do here?
            e.printStackTrace();
        }
        return null;
    }
}
