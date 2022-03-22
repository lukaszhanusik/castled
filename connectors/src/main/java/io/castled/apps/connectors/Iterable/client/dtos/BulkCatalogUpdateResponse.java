package io.castled.apps.connectors.Iterable.client.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Map;

@Data
public class BulkCatalogUpdateResponse {

    private String msg;
    private String code;
    private Map<String, Object> params;
}
