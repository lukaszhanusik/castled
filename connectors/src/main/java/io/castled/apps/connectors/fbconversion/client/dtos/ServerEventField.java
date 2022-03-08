package io.castled.apps.connectors.fbconversion.client.dtos;

import lombok.Getter;

// Ref: https://developers.facebook.com/docs/marketing-api/conversions-api/parameters

public enum ServerEventField {

    EVENT_NAME("event name", "event_name", false),
    EVENT_TIME("event time", "event_time", false),
    EVENT_ID("event id", "event_id", true),
    EVENT_SOURCE_URL("event source url", "event_source_url", true),
    OPT_OUT("opt out", "opt_out", true),
    ACTION_SOURCE("action source", "action_source", true),
    DATA_PROC_OPT("data processing options", "data_processing_options", true),
    DATA_PROC_OPT_CT("country data processing options", "data_processing_options_country", true),
    DATA_PROC_OPT_ST("state data processing options", "data_processing_options_state", true);

    @Getter
    private String displayName;

    @Getter
    private String name;

    @Getter
    private boolean optional;

    ServerEventField(String displayName, String name, boolean optional) {
        this.name = name;
        this.displayName = displayName;
        this.optional = optional;
    }
}
