package com.github.onsdigital.logging.v2;

import javax.servlet.http.HttpServletRequest;

public interface Loggable {

    String namespace();

    Loggable event(String event);

    Loggable traceID(String traceID);

    Loggable spanID(String spanID);

    Loggable httpReq(HttpServletRequest req);

    Loggable httpMethod(String method);

    Loggable httpPath(String path);

    void log();
}
