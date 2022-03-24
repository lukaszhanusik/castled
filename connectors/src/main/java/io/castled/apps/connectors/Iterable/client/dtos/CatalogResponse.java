package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.Data;

@Data
public class CatalogResponse {

    private String msg;
    private String code;
    private CatalogNameParams params;
}
