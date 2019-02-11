package com.github.onsdigital.logging.v2.storage;

import com.github.onsdigital.logging.v2.event.Auth;
import com.github.onsdigital.logging.v2.event.HTTP;

import javax.servlet.http.HttpServletRequest;

public interface LogStore {

    void saveHTTP(HTTP http);

    void saveTraceID(HttpServletRequest req);

    void saveAuth(Auth auth);

    HTTP getHTTP();

    String getTraceID();
}
