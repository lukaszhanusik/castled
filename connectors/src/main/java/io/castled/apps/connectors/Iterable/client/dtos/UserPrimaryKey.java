package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserPrimaryKey {

    EMAIL("email"),
    USER_ID("userId");

    private final String name;
}
