package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HTTP {

    @JsonProperty("status_code")
    private Integer statusCode;

    private String method;
    private String path;
    private String query;
    private String scheme;
    private String host;
    private Integer port;
    private Long duration;

    public HTTP request(HttpServletRequest req) {
        if (req != null) {
            this.method = req.getMethod();
            this.path = req.getRequestURI();
            this.query = req.getQueryString();
            this.scheme = req.getScheme();
            this.host = req.getServerName();
            this.port = req.getServerPort();
        }
        return this;
    }

    public HTTP request(HTTP details) {
        if (details != null) {
            this.method = details.method;
            this.path = details.path;
            this.query = details.query;
            this.scheme = details.scheme;
            this.host = details.host;
            this.port = details.port;
        }
        return this;
    }

    public HTTP request(HttpUriRequest req) {
        if (req != null) {
            this.method = req.getMethod();
            this.path = req.getURI().getPath();
            this.query = req.getURI().getQuery();
            this.scheme = req.getURI().getScheme();
            this.host = req.getURI().getHost();
            this.port = req.getURI().getPort();
        }
        return this;
    }

    public HTTP response(HttpServletResponse resp) {
        if (resp != null) {
            this.statusCode = resp.getStatus();
        }
        return this;
    }

    public HTTP response(HttpResponse resp) {
        if (resp != null) {
            this.statusCode = resp.getStatusLine().getStatusCode();
        }
        return this;
    }

    public HTTP response(int status) {
        this.statusCode = statusCode;
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
}
