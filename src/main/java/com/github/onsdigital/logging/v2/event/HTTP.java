package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.ZonedDateTime;

import static spark.utils.StringUtils.isNotEmpty;

public class HTTP {

    @JsonProperty("status_code")
    private int statucCode;

    @JsonProperty("started_at")
    private ZonedDateTime startedAt;

    @JsonProperty("ended_at")
    private ZonedDateTime endedAt;

    private String method;
    private String path;
    private String query;
    private String scheme;
    private String host;
    private int port;
    private long duration;

    public void method(String method) {
        if (isNotEmpty(method)) {
            this.method = method;
        }
    }

    public void path(String path) {
        if (isNotEmpty(path)) {
            this.path = path;
        }
    }

    public void query(String query) {
        if (isNotEmpty(query)) {
            this.query = query;
        }
    }

    public void scheme(String scheme) {
        if (isNotEmpty(scheme)) {
            this.scheme = scheme;
        }
    }

    public void host(String host) {
        if (isNotEmpty(host)) {
            this.host = host;
        }
    }

    public void port(int port) {
        this.port = port;
    }

    public void statusCode(int statusCode) {
        this.statucCode = statusCode;
    }

    public void startedAt(ZonedDateTime startedAt) {
        if (startedAt != null) {
            this.startedAt = startedAt;
        }
    }

    public void endedAt(ZonedDateTime endedAt) {
        if (endedAt != null) {
            this.endedAt = endedAt;
        }
    }

    public void duration() {
        if (startedAt != null) {
            if (endedAt == null) {
                this.endedAt = ZonedDateTime.now();
            }
            this.duration = Duration.between(startedAt, endedAt).toNanos();
        }
    }
}
