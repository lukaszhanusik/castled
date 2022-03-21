package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.Data;

@Data
public class CatalogFieldMappingResponse {

    private String msg;
    private String code;
    private CatalogFieldMappingParams params;
}
