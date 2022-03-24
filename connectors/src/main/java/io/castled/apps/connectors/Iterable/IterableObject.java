package io.castled.apps.connectors.Iterable;

import lombok.Getter;

public enum IterableObject {

    USERS("users"),
    EVENTS("events"),
    CATALOGS("catalogs");

    @Getter
    private String name;

    IterableObject(String name) {
        this.name = name;
    }
}
