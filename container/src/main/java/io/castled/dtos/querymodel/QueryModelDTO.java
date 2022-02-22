package io.castled.dtos.querymodel;

import io.castled.models.QueryModelPK;
import lombok.Data;

@Data
public class QueryModelDTO {

    private Long warehouseId;

    private String modelName;

    private String modelType;

    private QueryModelDetails modelDetails;

    private QueryModelPK queryModelPK;
}
