package io.castled.dtos.querymodel;

import io.castled.models.QueryModelPK;
import io.castled.models.QueryModelType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ModelInputDTO {

    @NotNull
    private Long warehouseId;

    @NotNull
    private String name;

    private QueryModelType type;

    @NotNull
    private QueryModelDetails details;

    @NotNull
    private QueryModelPK queryPK;

    private boolean demo;
}
