package com.github.onsdigital.logging.v2.event;

import com.github.onsdigital.logging.v2.storage.LogStore;

public class ThirdPartyEvent extends BaseEvent {

    private String raw;

    public ThirdPartyEvent(String namespace, Severity severity, String raw, LogStore logStore) {
        super(namespace, severity, logStore);
        super.event = "third party log";
        this.raw = raw;
    }

    public String getRaw() {
        return this.raw;
    }
}
