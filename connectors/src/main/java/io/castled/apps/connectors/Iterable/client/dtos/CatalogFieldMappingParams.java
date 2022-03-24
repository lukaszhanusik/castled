package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CatalogFieldMappingParams {

    private Map<String, String> definedMappings;
    private List<String> undefinedFields;
}
