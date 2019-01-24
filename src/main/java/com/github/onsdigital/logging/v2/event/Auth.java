package com.github.onsdigital.logging.v2.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Auth {

    public enum IdentityType {

        @JsonProperty("user")
        USER,

        @JsonProperty("service")
        SERVICE
    }

    private String identity;

    @JsonProperty("identity_type")
    private IdentityType identityType;

    public void identity(String identity) {
        this.identity = identity;
    }

    public void typeUser() {
        this.identityType = IdentityType.USER;
    }

    public void typeService() {
        this.identityType = IdentityType.SERVICE;
    }

}
