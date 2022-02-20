package io.castled.apps.connectors.fbcustomaudience.client.dtos;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FbError {

    private String message;
    private String type;
    private Integer code;
    private Integer errorSubcode;
    private String fbtraceId;
}
