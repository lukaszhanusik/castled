package io.castled.apps.connectors.fbcustomaudience.client.dtos;

import lombok.Getter;

public enum FbAudienceUserFields {
    EXTERN_ID("extern_id", "EXTERN_ID", false),
    EMAIL("email", "EMAIL", true),
    PHONE("phone number", "PHONE", true),
    GEN("gender", "GEN", true),
    DOBY("birth year(YYYY)", "DOBY", true),
    DOBM("birth month(MM)", "DOBM", true),
    DOBD("birth day(DD)", "DOBD", true),
    LN("last name", "LN", true),
    FN("first name", "FN", true),
    ST("state", "ST", true),
    CT("city", "CT", true),
    ZIP("zip", "ZIP", true),
    COUNTRY("county", "COUNTRY", true),
    MADID("mobile advertiser id", "MADID", false);

    @Getter
    private final String displayName;

    @Getter
    private final String name;

    @Getter
    private final boolean hashable;

    FbAudienceUserFields(String displayName, String name, boolean hashable) {
        this.displayName = displayName;
        this.name = name;
        this.hashable = hashable;
    }
}
