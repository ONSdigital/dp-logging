package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.ZonedDateTime;

public class HTTP {

    @JsonProperty("status_code")
    private Integer statusCode;

    @JsonProperty("started_at")
    private ZonedDateTime startedAt;

    @JsonProperty("ended_at")
    private ZonedDateTime endedAt;

    private String method;
    private String path;
    private String query;
    private String scheme;
    private String host;
    private Integer port;
    private Long duration;

    /**
     * For a new http request being received by the app. Adds http request details to the log event.
     *
     * @param req the request to get the values from.
     * @return
     */
    public HTTP begin(HttpServletRequest req) {
        if (req != null) {
            this.method = req.getMethod();
            this.path = req.getRequestURI();
            this.query = req.getQueryString();
            this.scheme = req.getScheme();
            this.host = req.getServerName();
            this.port = req.getServerPort();
            this.startedAt = ZonedDateTime.now();
        }
        return this;
    }

    public HTTP begin(HTTP that) {
        if (that != null) {
            this.method = that.method;
            this.path = that.path;
            this.query = that.query;
            this.scheme = that.scheme;
            this.host = that.host;
            this.port = that.port;
            this.startedAt = ZonedDateTime.now();
        }
        return this;
    }

    public HTTP setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HTTP setMethod(String method) {
        this.method = method;
        return this;
    }

    public HTTP setPath(String path) {
        this.path = path;
        return this;
    }

    public HTTP setQuery(String query) {
        this.query = query;
        return this;
    }

    public HTTP setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public HTTP setHost(String host) {
        this.host = host;
        return this;
    }

    public HTTP setPort(Integer port) {
        this.port = port;
        return this;
    }

    /**
     * Capture http response details and add them to the HTTP event
     */
    public HTTP end(HttpServletResponse resp) {
        if (resp != null) {
            this.statusCode = resp.getStatus();
            this.endedAt = ZonedDateTime.now();
        }
        calcDuration();
        return this;
    }

    public HTTP end(int statusCode) {
        this.statusCode = statusCode;
        this.endedAt = ZonedDateTime.now();
        calcDuration();
        return this;
    }

    public HTTP calcDuration() {
        if (this.startedAt == null) {
            startedAt = ZonedDateTime.now();
        }

        if (endedAt == null) {
            this.endedAt = ZonedDateTime.now();
        }
        this.duration = Duration.between(startedAt, endedAt).toNanos();
        return this;
    }
}
