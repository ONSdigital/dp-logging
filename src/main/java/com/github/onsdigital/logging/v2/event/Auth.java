package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Auth {

    public static final String FLORENCE_TOKEN = "X-Florence-Token";

    public enum IdentityType {

        @JsonProperty("user")
        USER,

        @JsonProperty("service")
        SERVICE
    }

    private String identity;

    @JsonProperty("identity_type")
    private IdentityType identityType;

    public Auth identity(String identity) {
        this.identity = identity;
        return this;
    }

    public Auth typeUser(IdentityType identityType) {
        this.identityType = identityType;
        return this;
    }

    public void typeUser() {
        this.identityType = IdentityType.USER;
    }

    public void typeService() {
        this.identityType = IdentityType.SERVICE;
    }

}
