package io.castled.apps.connectors.intercom.client.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataAttribute {

    private String model;
    private String name;
    private String dataType;
    private boolean custom;
    private boolean apiWritable;
    private boolean archived;
    private String fullName;
    private String description;
}
