package com.github.onsdigital.logging.v2.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.onsdigital.logging.v2.LoggingException;
import com.github.onsdigital.logging.v2.event.Auth;
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
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.setPropertyNamingStrategy(new NamingStrategy());
        this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        if (isPretty) {
            this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        // default error mapper implementation
        this.errorEventMapper = (ex, message, event) -> {
            SimpleEvent err = error(message).exception(ex);
            if (event != null) {
                err.data("original_class", event.getClass().getName())
                        .data("original_event", event.getEvent());
            }
            return err;
        };
    }

    @Override
    public <T extends BaseEvent> String marshallEvent(T event) throws LoggingException {
        try {
            return toJson(event);
        } catch (Exception ex) {
            return toJson(errorEventMapper.map(ex, MARSHALL_ERROR_MSG, event));
        }
    }

    @Override
    public String marshallHTTP(HTTP http) throws LoggingException {
        return toJson(http);
    }

    @Override
    public HTTP unmarshallHTTP(String json) throws LoggingException {
        try {
            return mapper.readValue(json, HTTP.class);
        } catch (Exception e) {
            throw new LoggingException("error unmarshalling HTTP json to object", e);
        }
    }

    @Override
    public String marshallAuth(Auth auth) throws LoggingException {
        return toJson(auth);
    }

    @Override
    public Auth unmarshallAuth(String auth) throws LoggingException {
        try {
            return mapper.readValue(auth, Auth.class);
        } catch (Exception e) {
            throw new LoggingException("error unmarshalling Auth json to object", e);
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
