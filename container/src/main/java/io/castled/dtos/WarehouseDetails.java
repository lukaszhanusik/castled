package io.castled.dtos;

import io.castled.schema.models.FieldSchema;
import io.castled.warehouses.WarehouseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WarehouseDetails {

    private Long id;
    private String name;
    private WarehouseType type;
    private String logoUrl;
    private List<FieldSchema> columnDetails;
}
