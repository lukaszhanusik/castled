package io.castled.dtos.querymodel;

import io.castled.models.QueryModelPK;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryModelDTO {
    @NotNull
    private Long id;
    @NotNull
    private Long warehouseId;
    @NotNull
    private String modelName;
    @NotNull
    private QueryModelDetails modelDetails;
    @NotNull
    private QueryModelPK queryModelPK;
}
