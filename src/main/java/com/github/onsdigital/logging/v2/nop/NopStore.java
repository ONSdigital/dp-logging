package com.github.onsdigital.logging.v2.nop;

import com.github.onsdigital.logging.v2.event.Auth;
import com.github.onsdigital.logging.v2.storage.LogStore;

import javax.servlet.http.HttpServletRequest;

public class NopStore implements LogStore {

    @Override
    public void saveTraceID(HttpServletRequest req) {

    }

    @Override
    public void saveTraceID(String id) {

    }

    @Override
    public void saveAuth(Auth auth) {

    }

    @Override
    public String getTraceID() {
        return null;
    }

    @Override
    public Auth getAuth() {
        return null;
    }
}
