package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CatalogNameParams {

    private List<CatalogName> catalogNames;
    private long totalCatalogsCount;
}
