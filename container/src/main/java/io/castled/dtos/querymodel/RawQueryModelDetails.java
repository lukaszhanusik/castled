package io.castled.dtos.querymodel;

import io.castled.models.QueryModelType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawQueryModelDetails extends QueryModelDetails {

    private String sourceQuery;

    public RawQueryModelDetails(String sourceQuery) {
        super(QueryModelType.RAW_QUERY_EDITOR);
        this.sourceQuery = sourceQuery;
    }
}
