package io.castled.dtos.querymodel;

import io.castled.models.QueryModelPK;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ModelInputDTO {

    @NotNull
    private Long warehouseId;

    @NotNull
    private String modelName;

    private String modelType;

    @NotNull
    private QueryModelDetails modelDetails;

    @NotNull
    private QueryModelPK queryModelPK;
}
