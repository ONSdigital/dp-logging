package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.ZonedDateTime;

import static spark.utils.StringUtils.isNotEmpty;

public class HTTP {

    @JsonProperty("status_code")
    private Integer statucCode;

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

    public HTTP request(HttpServletRequest req) {
        if (req != null) {
            method(req.getMethod())
                    .path(req.getRequestURI())
                    .query(req.getQueryString())
                    .scheme(req.getScheme())
                    .host(req.getServerName())
                    .port(req.getServerPort())
                    .startedAt(ZonedDateTime.now());
        }
        return this;
    }

    public HTTP response(HttpServletResponse resp) {
        if (resp != null) {
            statusCode(resp.getStatus())
                    .endedAt(ZonedDateTime.now())
                    .duration();
        }
        return this;
    }

    public HTTP method(String method) {
        if (isNotEmpty(method)) {
            this.method = method;
        }
        return this;
    }

    public HTTP path(String path) {
        if (isNotEmpty(path)) {
            this.path = path;
        }
        return this;
    }

    public HTTP query(String query) {
        if (isNotEmpty(query)) {
            this.query = query;
        }
        return this;
    }

    public HTTP scheme(String scheme) {
        if (isNotEmpty(scheme)) {
            this.scheme = scheme;
        }
        return this;
    }

    public HTTP host(String host) {
        if (isNotEmpty(host)) {
            this.host = host;
        }
        return this;
    }

    public HTTP port(int port) {
        this.port = port;
        return this;
    }

    public HTTP statusCode(int statusCode) {
        this.statucCode = statusCode;
        return this;
    }

    public HTTP startedAt(ZonedDateTime startedAt) {
        if (startedAt != null) {
            this.startedAt = startedAt;
        }
        return this;
    }

    public HTTP endedAt(ZonedDateTime endedAt) {
        if (endedAt != null) {
            this.endedAt = endedAt;
        }
        return this;
    }

    public HTTP duration() {
        if (startedAt != null) {
            if (endedAt == null) {
                this.endedAt = ZonedDateTime.now();
            }
            this.duration = Duration.between(startedAt, endedAt).toNanos();
        }
        return this;
    }
}
