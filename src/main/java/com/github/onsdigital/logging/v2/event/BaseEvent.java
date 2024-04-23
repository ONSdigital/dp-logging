package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.onsdigital.logging.v2.DPLogger;
import com.github.onsdigital.logging.v2.storage.LogStore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;

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
    private List<Error> errors;

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

    /**
     * Set the trace ID for this log event. If a trace ID already exists it will be overwritten, if a
     * null or empty ID is provided then a new ID will be generated.
     *
     * @param id the ID to set.
     * @return
     */
    public T traceID(String id) {
        this.traceID = store.saveTraceID(id);
        return (T) this;
    }

    /**
     * Capture the inbound request deatils and store the trace ID if a header exists on the request.
     * If not a new ID will be generated and stored.
     *
     * @param req the {@link HttpServletRequest} to extract the request details from.
     * @return this instance of the event with the updated request details.
     */
    public T beginHTTP(HttpServletRequest req) {
        this.traceID = store.saveTraceID(req);
        this.http = new HTTP(req);
        return (T) this;
    }

    /**
     * Capture the request/response details for a completed request. **Note** a completed request can
     * be successful or unsuccessful.
     *
     * @param req  the {@link HttpServletRequest} to extract the request details from.
     * @param resp the {@link HttpServletResponse} to extract the response details from.
     * @return this instance of the event with the updated request/response details.
     */
    public T endHTTP(HttpServletRequest req, HttpServletResponse resp) {
        this.http = new HTTP(req, resp);
        this.traceID = store.getTraceID();
        return (T) this;
    }

    /**
     * Capture the inbound request deatils and store the trace ID if a header exists on the request.
     * If not a new ID will be generated and stored.
     *
     * @param req the {@link HttpUriRequest} to extract the request details from.
     * @return this instance of the event with the updated request details.
     */
    public T beginHTTP(HttpUriRequest req) throws URISyntaxException {
        this.traceID = store.saveTraceID(req);
        this.http = new HTTP(req);
        return (T) this;
    }

    /**
     * Capture the request/response details for a completed request. **Note** a completed request can
     * be successful or unsuccessful.
     *
     * @param req  the {@link HttpServletRequest} to extract the request details from.
     * @param resp the {@link HttpServletResponse} to extract the response details from.
     * @return this instance of the event with the updated request/response details.
     */
    public T endHTTP(HttpUriRequest req, HttpResponse resp) throws URISyntaxException {
        this.http = new HTTP(req, resp);
        this.traceID = store.getTraceID();
        return (T) this;
    }

    public T userIdentity(String identity) {
        return authIdenity(identity, Auth.IdentityType.USER);
    }

    public T serviceIdentity(String identity) {
        return authIdenity(identity, Auth.IdentityType.SERVICE);
    }

    private T authIdenity(String identity, Auth.IdentityType identityType) {
        this.auth = this.getAuthSafe().identity(identity).typeUser(identityType);
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
            this.errors = new Errors(t).getErrors();
        }
        return (T) this;
    }

    public <T extends Throwable> T logException(T t, String event) {
        exception(t);
        log(event);
        return t;
    }

    public <T extends Throwable> void logAndThrow(T t, String event) throws T {
        exception(t);
        log(event);
        throw t;
    }

    /**
     * Log this event.
     *
     * @param event a human readable description of the event.
     */
    public void log(String event) {
        this.event = event;

        if (StringUtils.isEmpty(traceID)) {
            this.traceID = store.getTraceID();
        }

        if (auth == null) {
            this.auth = store.getAuth();
        }

        DPLogger.log(this);
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

    public List<Error> getErrors() {
        return this.errors;
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
                .append("errors", errors)
                .toString();
    }
}
