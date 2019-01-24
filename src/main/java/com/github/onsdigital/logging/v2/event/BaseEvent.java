package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
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
    private ZonedDateTime createAt;

    @JsonProperty("trace_id")
    private String traceID;

    @JsonProperty("span_id")
    private String spanID;

    private int severity;

    private HTTP http;

    private Map<String, Object> auth;

    private Map<String, Object> data;

    private String event;

    protected BaseEvent(String namespace, Severity severity) {
        this.createAt = ZonedDateTime.now();
        this.namespace = namespace;
        this.severity = severity == null ? Severity.INFO.getLevel() : severity.getLevel();

        this.http = new HTTP();
        this.auth = new ValidatingMap();
        this.data = new ValidatingMap();
    }

    public T httpMethod(String method) {
        http.method(method);
        return (T) this;
    }

    public T httpPath(String path) {
        http.path(path);
        return (T) this;
    }

    public T httpQuery(String query) {
        http.query(query);
        return (T) this;
    }

    public T httpScheme(String scheme) {
        http.scheme(scheme);
        return (T) this;
    }

    public T httpHost(String host) {
        http.host(host);
        return (T) this;
    }

    public T httpPort(int port) {
        http.port(port);
        return (T) this;
    }

    public T httpStatusCode(int statusCode) {
        http.statusCode(statusCode);
        return (T) this;
    }

    public T httpStartedAt(ZonedDateTime startedAt) {
        http.startedAt(startedAt);
        return (T) this;
    }

    public T httpEndedAt(ZonedDateTime endedAt) {
        http.endedAt(endedAt);
        return (T) this;
    }

    public T httpDuration() {
        http.duration();
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
