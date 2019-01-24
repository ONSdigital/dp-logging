package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.onsdigital.logging.v2.time.LogEventUtil;

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
            this.startedAt = LogEventUtil.setHTTPStartedAt(ZonedDateTime.now());
        }
        return this;
    }

    /**
     * Capture http response details and add them to the HTTP event
     *
     * @param req the request to get the values from.
     * @return
     */
    public HTTP end(HttpServletRequest req, HttpServletResponse resp) {
        if (req != null) {
            this.method = req.getMethod();
            this.path = req.getRequestURI();
            this.query = req.getQueryString();
            this.scheme = req.getScheme();
            this.host = req.getServerName();
            this.port = req.getServerPort();
            this.startedAt = LogEventUtil.getHTTPStartedAt();
        }
        if (resp != null) {
            this.statusCode = resp.getStatus();
            this.endedAt = ZonedDateTime.now();
            calcDuration();
        }
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
