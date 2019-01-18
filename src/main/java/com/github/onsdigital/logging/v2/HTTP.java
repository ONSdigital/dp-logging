package com.github.onsdigital.logging.v2;

import javax.servlet.http.HttpServletRequest;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class HTTP {

    private String method;
    private String path;
    private String query;
    private String scheme;
    private String host;

    public HTTP httpReq(HttpServletRequest req) {
        return this.method(req.getMethod()).path(req.getRequestURI());
    }

    public HTTP method(String method) {
        this.method = method;
        return this;
    }

    public HTTP path(String path) {
        this.path = path;
        return this;
    }

    public HTTP query(String query) {
        this.query = query;
        return this;
    }

    public HTTP scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public HTTP host(String host) {
        this.host = host;
        return this;
    }

    String method() {
        return this.method;
    }

    String path() {
        return this.path;
    }

    String query() {
        return this.query;
    }

    String scheme() {
        return this.scheme;
    }

    String host() {
        return this.host;
    }

    boolean empty() {
        return isEmpty(method)
                || isEmpty(path)
                || isEmpty(query)
                || isEmpty(scheme)
                || isEmpty(host);
    }
}
