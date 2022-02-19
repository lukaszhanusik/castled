package io.castled.apps.connectors.fbcustomaudience.client.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Map;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CustomerListResponse {

    String audienceId;
    String sessionId;
    Integer numReceived;
    Integer numInvalidEntries;
    Map<String, String> invalidEntrySamples;
}
