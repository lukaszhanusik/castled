package io.castled.apps.connectors.fbconversion.client.dtos;

import lombok.Getter;

// Ref: https://developers.facebook.com/docs/marketing-api/conversions-api/parameters

public enum CustomDataField {

    VALUE( "value", "value"),
    CURRENCY("currency", "currency"),
    CONTENT_NAME("content name", "content_name"),
    CONTENT_CATEGORY("content category", "content_category"),
    CONTENT_IDS("content ids", "content_ids"),
    CONTENTS("contents", "contents"),
    CONTENT_TYPE("content type", "content_type"),
    ORDER_ID("order id", "order_id"),
    PREDICTED_LTV("predicted ltv", "predicted_ltv"),
    NUM_ITEMS("num items","num_items"),
    SEARCH_STRING("search string", "search_string"),
    STATUS("status", "status"),
    DELIVERY_CATEGORY("delivery category", "delivery_category");

    @Getter
    private String displayName;

    @Getter
    private String name;

    CustomDataField(String displayName, String name) {
        this.displayName = displayName;
        this.name = name;
    }
}
