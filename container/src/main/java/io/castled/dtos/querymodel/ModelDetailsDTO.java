package io.castled.dtos.querymodel;

import io.castled.dtos.WarehouseDetails;
import io.castled.models.QueryModelPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelDetailsDTO {
    private Long id;
    private Long userId;
    private Long teamId;
    private WarehouseDetails warehouse;
    private String modelName;
    private String modelType;
    private QueryModelDetails modelDetails;
    private QueryModelPK queryModelPK;
    private Integer activeSyncsCount;
}
