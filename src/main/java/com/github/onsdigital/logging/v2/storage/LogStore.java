package com.github.onsdigital.logging.v2.storage;

import com.github.onsdigital.logging.v2.event.Auth;

import javax.servlet.http.HttpServletRequest;

public interface LogStore {

    void saveTraceID(HttpServletRequest req);

    void saveTraceID(String id);

    void saveAuth(Auth auth);

    String getTraceID();

    Auth getAuth();
}
