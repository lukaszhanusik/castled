package io.castled.dtos;

import io.castled.warehouses.WarehouseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WarehouseDetails {

    private Long id;
    private String name;
    private WarehouseType type;
    private String logoUrl;
}
