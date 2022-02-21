package io.castled.apps.connectors.fbcustomaudience.client.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Map;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CustomerListResponse {

    private String audienceId;
    private String sessionId;
    private Integer numReceived;
    private Integer numInvalidEntries;
    private Map<String, String> invalidEntrySamples;
}
