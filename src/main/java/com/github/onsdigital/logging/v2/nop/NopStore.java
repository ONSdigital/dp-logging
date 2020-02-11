package com.github.onsdigital.logging.v2.nop;

import com.github.onsdigital.logging.v2.event.Auth;
import com.github.onsdigital.logging.v2.storage.LogStore;
import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;

public class NopStore implements LogStore {

    @Override
    public String saveTraceID(HttpServletRequest req) {
        return "";
    }

    @Override
    public String saveTraceID(HttpUriRequest httpUriRequest) {
        return "";
    }

    @Override
    public String saveTraceID(String id) {
        return "";
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
