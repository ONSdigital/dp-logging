package com.github.onsdigital.logging.v2.storage;

import com.github.onsdigital.logging.v2.event.Auth;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;

public interface LogStore {

    String saveTraceID(HttpServletRequest req);

    String saveTraceID(HttpUriRequest httpUriRequest);

    String saveTraceID(String id);

    void saveAuth(Auth auth);

    String getTraceID();

    Auth getAuth();
}
