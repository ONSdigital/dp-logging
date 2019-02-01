package com.github.onsdigital.logging.v2.event;

public class ThirdPartyEvent extends BaseEvent {

    private String raw;

    public ThirdPartyEvent(String namespace, Severity severity, String raw) {
        super(namespace, severity);
        super.event = "third party log";
        this.raw = raw;
    }

    public String getRaw() {
        return this.raw;
    }
}
