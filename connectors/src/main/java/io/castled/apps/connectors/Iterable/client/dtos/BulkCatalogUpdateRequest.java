package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class BulkCatalogUpdateRequest {

    private Map<String, Map<String, Object>> documents;
    private boolean replaceUploadedFieldsOnly;
}
