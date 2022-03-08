package io.castled.apps.connectors.fbconversion.client.dtos;

import lombok.Getter;

public enum ServerEventAggregatedField {

    USER_DATA("user data", "user_data"),
    CUSTOM_DATA("custom_data", "custom_data");

    @Getter
    private String displayName;

    @Getter
    private String name;

    ServerEventAggregatedField(String displayName, String name) {
        this.displayName = displayName;
        this.name = name;
    }
}
