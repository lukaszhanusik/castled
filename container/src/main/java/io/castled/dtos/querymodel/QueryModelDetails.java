package io.castled.dtos.querymodel;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.castled.models.QueryModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        visible = true,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RawQueryModelDetails.class, name = "RAW_QUERY"),
        @JsonSubTypes.Type(value = TableQueryModelDetails.class, name = "TABLE_SELECTOR"),
        @JsonSubTypes.Type(value = DbtQueryModelDetails.class, name = "DBT_MODEL_SELECTOR")})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class QueryModelDetails {

    private QueryModelType modelType;
}
