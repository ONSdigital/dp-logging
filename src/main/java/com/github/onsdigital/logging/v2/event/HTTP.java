package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * POJO containing HTTP details of requests/resopnses required by log events.
 */
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

    /**
     * Construct a new HTTP instance and populate the request fields using the provided {@link
     * HttpServletRequest}. The response fields will be null.
     *
     * @param req a {@link HttpServletRequest} to extract the request fields from.
     */
    public HTTP(HttpServletRequest req) {
        this(req, null);
    }

    /**
     * Construct a new HTTP instance and populate the request and response fields using the provided
     * {@link HttpServletRequest} and {@link HttpServletResponse}.
     *
     * @param req  the {@link HttpServletRequest} to extract the request fields from.
     * @param resp the {@link HttpServletResponse} to extract the response fields from.
     */
    public HTTP(HttpServletRequest req, HttpServletResponse resp) {
        if (req != null) {
            this.method = defaultIfEmpty(req.getMethod(), "");
            this.path = defaultIfEmpty(req.getRequestURI(), "");
            this.query = defaultIfEmpty(req.getQueryString(), "");
            this.scheme = defaultIfEmpty(req.getScheme(), "");
            this.host = defaultIfEmpty(req.getServerName(), "");
            this.port = req.getServerPort();
        }

        if (resp != null) {
            this.statusCode = resp.getStatus();
        }
    }

    /**
     * Construct a new HTTP instance and populate the request fields using the provided {@link
     * HttpUriRequest}. The response fields will be null.
     *
     * @param req a {@link HttpUriRequest} to extract the request fields from.
     */
    public HTTP(HttpUriRequest req) throws URISyntaxException {
        this(req, null);
    }

    /**
     * Construct a new HTTP instance and populate the request and response fields using the provided
     * {@link HttpUriRequest} and {@link HttpResponse}.
     *
     * @param req  the {@link HttpUriRequest} to extract the request fields from.
     * @param resp the {@link HttpResponse} to extract the response fields from.
     */
    public HTTP(HttpUriRequest req, HttpResponse resp) throws URISyntaxException {
        if (req != null) {
           this.method = req.getMethod();

            URI uri = req.getUri();
            if (uri != null) {
                this.path = defaultIfBlank(uri.getPath(), "");
                this.query = defaultIfBlank(uri.getQuery(), "");
                this.scheme = defaultIfBlank(uri.getScheme(), "");
                this.host = defaultIfBlank(uri.getHost(), "");
                this.port = uri.getPort();
            }
        }

        if (resp != null) {
            this.statusCode = resp.getCode();
        }
    }

    public Integer getStatusCode() {
        return this.statusCode;
    }

    public String getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.path;
    }

    public String getQuery() {
        return this.query;
    }

    public String getScheme() {
        return this.scheme;
    }

    public String getHost() {
        return this.host;
    }

    public Integer getPort() {
        return this.port;
    }

    public Long getDuration() {
        return this.duration;
    }
}
