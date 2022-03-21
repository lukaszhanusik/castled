package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventField {

    EMAIL("email", "string", true),
    EVENT_NAME("eventName", "string", false),
    ID("id", "string", true),
    CREATED_AT("createdAt", "integer", true),
    USER_ID("userId", "string", true),
    CAMPAIGN_ID("campaignId", "integer", true),
    TEMPLATE_ID("templateId", "integer", true);

    private String name;
    private String type;
    private boolean optional;
}
