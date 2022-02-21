package io.castled.dtos.querymodel;

import io.castled.models.QueryModelType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableQueryModelDetails extends QueryModelDetails {

    private String schemaName;
    private String tableName;
    private String sourceQuery;

    public TableQueryModelDetails(String schemaName, String tableName, String sourceQuery) {
        super(QueryModelType.TABLE_SELECTOR);
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.sourceQuery = sourceQuery;
    }
}
