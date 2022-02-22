package io.castled.dtos.querymodel;

import io.castled.models.QueryModelType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SqlQueryModelDetails extends QueryModelDetails {

    private String sourceQuery;

    public SqlQueryModelDetails(String sourceQuery) {
        super(QueryModelType.SQL_QUERY_EDITOR);
        this.sourceQuery = sourceQuery;
    }
}
