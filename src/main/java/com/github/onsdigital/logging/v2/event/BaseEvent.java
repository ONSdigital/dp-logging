package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.onsdigital.logging.v2.DPLogger;
import com.github.onsdigital.logging.v2.storage.LogStore;
import org.apache.commons.lang3.StringUtils;
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

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private int severity;

    private SafeMap data;
    private String namespace;
    private HTTP http;
    private Auth auth;
    private Error error;
    private Errors errors;

    protected String event;
    protected transient LogStore store;

    protected BaseEvent(String namespace, Severity severity, LogStore store) {
        this.createAt = ZonedDateTime.now();
        this.namespace = namespace;
        this.severity = severity == null ? Severity.INFO.getLevel() : severity.getLevel();
        this.data = new SafeMap();
        this.store = store;
    }

    protected BaseEvent(String namespace, Severity severity, LogStore store, String event) {
        this(namespace, severity, store);
        this.event = event;
    }

    public T traceID(String id) {
        // A trace id might already exist generated by the parent request. If so use that to tie the request together
        // otherwise generate a new ID.
        if (StringUtils.isNotEmpty(id)) {
            this.traceID = id;
            store.saveTraceID(id);
        }
        return (T) this;
    }

    public T beginHTTP(HttpServletRequest req) {
        store.saveTraceID(req);
        getHTPPSafe().begin(req);
        store.saveHTTP(http);
        return (T) this;
    }

    public T beginHTTP(HTTP httpDetails) {
        traceID(store.getTraceID());
        this.http = new HTTP().begin(httpDetails);
        store.saveHTTP(http);
        return (T) this;
    }

    public T endHTTP(HttpServletResponse resp) {
        this.http = store.getHTTP();
        if (this.http != null) {
            this.http.end(resp);
        }
        return (T) this;
    }

    public T endHTTP(int statusCode) {
        this.http = store.getHTTP();
        if (this.http != null) {
            this.http.end(statusCode);
        }
        return (T) this;
    }

    public T userIdentity(String identity) {
        return authIdenity(identity, Auth.IdentityType.USER);
    }

    public T serviceIdentity(String identity) {
        return authIdenity(identity, Auth.IdentityType.SERVICE);
    }

    private T authIdenity(String identity, Auth.IdentityType identityType) {
        this.auth = this.getAuthSafe()
                .identity(identity)
                .typeUser(identityType);
        store.saveAuth(auth);
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
        if (null != t) {
            this.error = new Error(t);
        }
        return (T) this;
    }

    public T exceptionAll(Throwable t) {
        if (null != t) {
            this.errors = new Errors(t);
        }
        return (T) this;
    }

    public <T extends Throwable> T logException(T t, String event) {
        if (t != null) {
            this.error = new Error(t);
        }
        log(event);
        return t;
    }

    public <T extends Throwable> void logAndThrow(T t, String event) throws T {
        if (t != null) {
            this.error = new Error(t);
        }
        log(event);
        throw t;
    }

    public void log(String event) {
        this.event = event;
        this.traceID = store.getTraceID();
        if (http == null) {
            this.http = store.getHTTP();
        }

        if (auth == null) {
            this.auth = store.getAuth();
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
                .append("event", event)
                .append("namespace", namespace)
                .append("severity", severity)
                .append("http", http)
                .append("auth", auth)
                .append("error", error)
                .append("errors", errors)
                .toString();
    }
}
