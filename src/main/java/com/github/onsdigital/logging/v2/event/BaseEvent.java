package com.github.onsdigital.logging.v2.event;

import org.codehaus.jackson.annotate.JsonProperty;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public abstract class BaseEvent<T extends BaseEvent> {

    // HTTP map keys
    static final String HTTP_METHOD = "method";
    static final String HTTP_PATH = "path";
    static final String HTTP_QUERY = "query";
    static final String HTTP_SCHEME = "scheme";
    static final String HTTP_HOST = "host";
    static final String HTTP_PORT = "port";
    static final String HTTP_STATUS_CODE = "status_code";
    static final String HTTP_STARTED_AT = "started_at";
    static final String HTTP_ENDED_AT = "ended_at";
    static final String HTTP_DURATION = "duration";

    private String namespace;

    // TODO convert thus to ISO8601 format
    @JsonProperty("created_at")
    private Date createAt;

    @JsonProperty("trace_id")
    private String traceID;

    @JsonProperty("span_id")
    private String spanID;

    private int severity;

    private Map<String, Object> http;

    private Map<String, Object> auth;

    private Map<String, Object> data;

    private String event;

    protected BaseEvent(String namespace, Severity severity) {
        this.createAt = new Date();
        this.namespace = namespace;
        this.severity = severity == null ? Severity.INFO.getLevel() : severity.getLevel();

        this.http = new ValidatingMap();
        this.auth = new ValidatingMap();
        this.data = new ValidatingMap();
    }

    public T httpMethod(String method) {
        http.put(HTTP_METHOD, method);
        return (T) this;
    }

    public T httpPath(String path) {
        http.put(HTTP_PATH, path);
        return (T) this;
    }

    public T httpQuery(String query) {
        http.put(HTTP_QUERY, query);
        return (T) this;
    }

    public T httpScheme(String scheme) {
        http.put(HTTP_SCHEME, scheme);
        return (T) this;
    }

    public T httpHost(String host) {
        http.put(HTTP_HOST, host);
        return (T) this;
    }

    public T httpPort(int port) {
        http.put(HTTP_PORT, port);
        return (T) this;
    }

    public T httpStatusCode(int statusCode) {
        http.put(HTTP_STATUS_CODE, statusCode);
        return (T) this;
    }

    public T httpStartedAt(LocalDateTime startedAt) {
        http.put(HTTP_STARTED_AT, startedAt);
        return (T) this;
    }

    public T httpEndedAt(LocalDateTime endedAt) {
        http.put(HTTP_ENDED_AT, endedAt);
        return (T) this;
    }

    public T httpDuration() {
        if (http.containsKey(HTTP_STARTED_AT) && http.containsKey(HTTP_ENDED_AT)) {
            LocalDateTime s = (LocalDateTime) http.get(HTTP_STARTED_AT);
            LocalDateTime e = (LocalDateTime) http.get(HTTP_ENDED_AT);
            http.put(HTTP_DURATION, Duration.between(s, e).toNanos());
        }
        return (T) this;
    }

    private static class ValidatingMap extends HashMap<String, Object> {
        @Override
        public Object put(String key, Object value) {
            if (isNotEmpty(key) && value != null) {
                super.put(key, value);
            }
            return value;
        }
    }
}
