package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.onsdigital.logging.v2.DPLogger;
import com.github.onsdigital.logging.v2.time.ThreadStorage;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
    protected String event;
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
        ThreadStorage.storeTraceID(req);
        getHTPPSafe().begin(req);
        ThreadStorage.storeHTTP(http);
        return (T) this;
    }

    public T endHTTP(HttpServletResponse resp) {
        this.http = ThreadStorage.retrieveHTTP();
        this.http.end(resp);
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

    public T exception(Throwable t) {
        this.error = new Error(t);
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
        this.traceID = ThreadStorage.retrieveTraceID();
        if (http == null) {
            this.http = ThreadStorage.retrieveHTTP();
        }
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

    public ZonedDateTime getCreateAt() {
        return this.createAt;
    }

    public String getTraceID() {
        return this.traceID;
    }

    public String getSpanID() {
        return this.spanID;
    }

    public SafeMap getData() {
        return this.data;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public String getEvent() {
        return this.event;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public int getSeverity() {
        return this.severity;
    }

    public HTTP getHttp() {
        return this.http;
    }

    public Auth getAuth() {
        return this.auth;
    }

    public Error getError() {
        return this.error;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("createAt", createAt)
                .append("traceID", traceID)
                .append("spanID", spanID)
                .append("data", data)
                .append("throwable", throwable)
                .append("event", event)
                .append("namespace", namespace)
                .append("severity", severity)
                .append("http", http)
                .append("auth", auth)
                .append("error", error)
                .toString();
    }
}
