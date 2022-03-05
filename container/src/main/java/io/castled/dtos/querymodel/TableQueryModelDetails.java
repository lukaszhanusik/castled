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

    public TableQueryModelDetails() {
        super(QueryModelType.TABLE);
    }
}
