package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CatalogItemField {

    ITEM_ID("itemId", "string");

    private String name;
    private String type;
}
