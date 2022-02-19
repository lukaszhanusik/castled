package io.castled.apps.connectors.fbcustomaudience.client.dtos;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FbError {
    String message;
    String type;
    Integer code;
    Integer errorSubcode;
    String fbtraceId;
}
