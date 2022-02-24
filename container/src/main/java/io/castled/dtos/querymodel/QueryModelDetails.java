package io.castled.dtos.querymodel;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.castled.models.QueryModelType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        visible = true,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SqlQueryModelDetails.class, name = "SQL_QUERY_EDITOR"),
        @JsonSubTypes.Type(value = TableQueryModelDetails.class, name = "TABLE_SELECTOR"),
        @JsonSubTypes.Type(value = DbtQueryModelDetails.class, name = "DBT_MODEL_SELECTOR")})
@NoArgsConstructor
@AllArgsConstructor
public abstract class QueryModelDetails {

    private QueryModelType type;
}
