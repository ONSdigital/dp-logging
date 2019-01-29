package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.onsdigital.logging.v2.DPLogger;
import com.github.onsdigital.logging.v2.time.LogEventUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;

@JsonPropertyOrder({"created_at", "namespace", "severity", "event", "trace_id", "span_id"})
public abstract class BaseEvent<T extends BaseEvent> {

    @JsonProperty("created_at")
    private ZonedDateTime createAt;

    @JsonProperty("trace_id")
    private String traceID;

    @JsonProperty("span_id")
    private String spanID;

    private SafeMap data;

    protected transient Throwable throwable;
    private String event;
    private String namespace;
    private int severity;
    private HTTP http;
    private Auth auth;
    private Error error;


    protected BaseEvent(String namespace, Severity severity) {
        this.createAt = ZonedDateTime.now();
        this.namespace = namespace;
        this.severity = severity == null ? Severity.INFO.getLevel() : severity.getLevel();

        this.data = new SafeMap();
    }

    public T beginHTTP(HttpServletRequest req) {
        getHTPPSafe().begin(req);
        return (T) this;
    }

    public T endHTTP(HttpServletRequest req, HttpServletResponse resp) {
        getHTPPSafe().end(req, resp);
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

    public T data(String key, Object value) {
        this.data.put(key, value);
        return (T) this;
    }

    public <T extends Throwable> T logAndThrow(T t, String event) {
        this.throwable = t;
        if (t != null) {
            this.error = new Error(t);
        }
        log(event);
        return t;
    }

    public void log(String event) {
        this.event = event;
        this.traceID = LogEventUtil.getTraceID();
        DPLogger.log(this);
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
}
