package io.castled.models;

import io.castled.dtos.querymodel.QueryModelDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryModel {
    private Long id;
    private Long userId;
    private Long teamId;
    private Long warehouseId;
    private String modelName;
    private String modelType;
    private QueryModelDetails modelDetails;
    private QueryModelPK queryModelPK;
}
