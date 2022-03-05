package io.castled.models;

import io.castled.dtos.querymodel.QueryModelDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryModel {
    private Long id;
    private Long userId;
    private Long teamId;
    private Long warehouseId;
    private String name;
    private QueryModelType type;
    private QueryModelDetails details;
    private QueryModelPK queryPK;
    private boolean demo;
}
