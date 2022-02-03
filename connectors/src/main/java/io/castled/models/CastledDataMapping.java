package io.castled.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.List;


@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        visible = true,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TargetFieldsMapping.class, name = "TARGET_FIELDS_MAPPING"),
        @JsonSubTypes.Type(value = TargetTemplateMapping.class, name = "TARGET_TEMPLATE_MAPPING")})
public class CastledDataMapping {

    private List<String> primaryKeys;
    private DataMappingType type;


}
