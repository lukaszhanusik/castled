package io.castled.apps.connectors.fbconversion.client.dtos;

import lombok.Getter;

// Ref: https://developers.facebook.com/docs/marketing-api/conversions-api/parameters

public enum CustomerInfoField {

    EMAIL("email", "em", true),
    PHONE("phone number", "ph", true),
    GEN("gender", "ge", true),
    DB("date of birth", "db", true),
    LN("last name", "ln", true),
    FN("first name", "fn", true),
    ST("state", "st", true),
    CT("city", "ct", true),
    ZIP("zip", "zp", true),
    COUNTRY("county", "country", true),
    EXTERN_ID("external ID", "external_id", true),
    CLIENT_IP("client IP address", "client_ip_address", false),
    CLIENT_USER("client user agent", "client_user_agent", false),
    CLICK_ID("click ID", "fbc", false),
    BROWSER_ID("browser ID", "fbp", false),
    SUB_ID("subscription ID", "subscription_id", false),
    LOGIN_ID("facebook login ID", "fb_login_id", false),
    LEAD_ID("lead ID", "lead_id", false);

    @Getter
    private String displayName;

    @Getter
    private String name;

    @Getter boolean hashable;

    CustomerInfoField(String displayName, String name, boolean hashable) {
        this.displayName = displayName;
        this.name = name;
        this.hashable = hashable;
    }

}
