package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.github.onsdigital.logging.v2.DPLogger.getEventSerialiser;
import static com.github.onsdigital.logging.v2.DPLogger.getLogger;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@JsonPropertyOrder({"created_at", "namespace", "severity", "event", "trace_id", "span_id"})
public abstract class BaseEvent<T extends BaseEvent> {

    private String namespace;

    @JsonProperty("created_at")
    private ZonedDateTime createAt;

    @JsonProperty("trace_id")
    private String traceID;

    @JsonProperty("span_id")
    private String spanID;

    private int severity;

    private HTTP http;

    private Auth auth;

    private Map<String, Object> data;

    private String event;

    protected BaseEvent(String namespace, Severity severity) {
        this.createAt = ZonedDateTime.now();
        this.namespace = namespace;
        this.severity = severity == null ? Severity.INFO.getLevel() : severity.getLevel();

        this.data = new ValidatingMap();
    }

    public T requst(HttpServletRequest req) {
        getHTPPSafe().request(req);
        return (T) this;
    }

    public T response(HttpServletResponse resp) {
        getHTPPSafe().response(resp);
        return (T) this;
    }

    public T httpMethod(String method) {
        getHTPPSafe().method(method);
        return (T) this;
    }

    public T httpPath(String path) {
        getHTPPSafe().path(path);
        return (T) this;
    }

    public T httpQuery(String query) {
        getHTPPSafe().query(query);
        return (T) this;
    }

    public T httpScheme(String scheme) {
        getHTPPSafe().scheme(scheme);
        return (T) this;
    }

    public T httpHost(String host) {
        getHTPPSafe().host(host);
        return (T) this;
    }

    public T httpPort(int port) {
        getHTPPSafe().port(port);
        return (T) this;
    }

    public T httpStatusCode(int statusCode) {
        getHTPPSafe().statusCode(statusCode);
        return (T) this;
    }

    public T httpStartedAt(ZonedDateTime startedAt) {
        getHTPPSafe().startedAt(startedAt);
        return (T) this;
    }

    public T httpEndedAt(ZonedDateTime endedAt) {
        getHTPPSafe().endedAt(endedAt);
        return (T) this;
    }

    public T httpDuration() {
        getHTPPSafe().duration();
        return (T) this;
    }

    public T authIdenity(String identity) {
        this.getAuthSafe().identity(identity);
        return (T) this;
    }

    public T authIdentityTypeUser() {
        this.getAuthSafe().typeUser();
        return (T) this;
    }

    public T authIdentityTypeService() {
        this.getAuthSafe().typeService();
        return (T) this;
    }

    public void log(String event) {
        this.event = event;
        getLogger().info(getEventSerialiser().toJson(this));
    }

    private HTTP getHTPPSafe() {
        if (this.http == null) {
            this.http = new HTTP();
        }
        return http;
    }

    private Auth getAuthSafe() {
        if (this.auth == null) {
            this.auth = new Auth();
        }
        return auth;
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
