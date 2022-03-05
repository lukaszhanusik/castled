package io.castled.apps.connectors.googleads;

import lombok.Getter;

public enum CustomerMatchType {
    CONTACT_INFO("Email, Phone and/or Mailing Addresses"),
    CRM_ID("User Ids"),
    MOBILE_ADVERTISING_ID("Mobile Device Ids");

    CustomerMatchType(String displayName) {
        this.displayName = displayName;
    }

    @Getter
    private final String displayName;
}
