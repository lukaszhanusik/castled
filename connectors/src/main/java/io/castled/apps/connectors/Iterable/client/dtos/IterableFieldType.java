package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IterableFieldType {

    STRING("string"),
    LONG("long"),
    INTEGER("integer"),
    DATE("date"),
    BOOLEAN("boolean"),
    OBJECT("object");

    private final String type;
}
