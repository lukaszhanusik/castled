package io.castled.apps.connectors.customerio;

import lombok.Getter;

public enum CIOEventTypeEnum {

    TRACK_EVENT("event"),
    TRACK_PAGE_VIEWS("pageView");

    @Getter
    private String eventType;

     CIOEventTypeEnum(String eventType){
        this.eventType = eventType;
    }
}
