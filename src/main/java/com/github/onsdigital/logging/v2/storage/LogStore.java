package com.github.onsdigital.logging.v2.storage;

import com.github.onsdigital.logging.v2.event.Auth;
import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;

public interface LogStore {

    void saveTraceID(HttpServletRequest req);

    void saveTraceID(HttpUriRequest httpUriRequest);

    void saveTraceID(String id);

    void saveAuth(Auth auth);

    String getTraceID();

    Auth getAuth();
}
