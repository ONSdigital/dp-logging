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

    static final String MARSHALL_ERROR_MSG = "failed to marshal log event to json";
    static final String EVENT_KEY = "event";

    private ObjectMapper mapper;

    private ErrorEventMapper errorEventMapper;

    public JacksonLogSerialiser() {
        this(false);
    }

    JacksonLogSerialiser(ObjectMapper mapper, ErrorEventMapper errorEventMapper) {
        this.mapper = mapper;
        this.errorEventMapper = errorEventMapper;
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

        // default error mapper implementation
        this.errorEventMapper = (ex, message, event) -> {
            SimpleEvent err = error(message).exception(ex);
            if (event != null) {
                err.data(EVENT_KEY, event.toString());
            }
            return err;
        };
    }

    @Override
    public <T extends BaseEvent> String marshall(T event) throws LoggingException {
        return toJson(event);
    }

    @Override
    public <T extends BaseEvent> String marshallWithRetry(T event) throws LoggingException {
        try {
            return toJson(event);
        } catch (Exception ex) {
            return toJson(errorEventMapper.map(ex, MARSHALL_ERROR_MSG, event));
        }
    }

    @Override
    public String marshall(HTTP http) throws LoggingException {
        return toJson(http);
    }

    @Override
    public HTTP getHTTP(String json) throws LoggingException {
        try {
            return mapper.readValue(json, HTTP.class);
        } catch (Exception e) {
            throw new LoggingException("error unmarshalling HTTP json to object", e);
        }
    }

    private String toJson(Object obj) throws LoggingException {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new LoggingException("error marshalling event to json", e);
        }
    }
}
