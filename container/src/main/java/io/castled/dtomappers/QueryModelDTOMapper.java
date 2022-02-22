package io.castled.dtomappers;

import io.castled.ObjectRegistry;
import io.castled.dtos.WarehouseDetails;
import io.castled.dtos.querymodel.ModelDetailsDTO;
import io.castled.models.QueryModel;
import io.castled.models.Warehouse;
import io.castled.warehouses.WarehouseService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QueryModelDTOMapper {
    QueryModelDTOMapper INSTANCE = Mappers.getMapper(QueryModelDTOMapper.class);

    @Mapping(target = "warehouse", source = "warehouseId", qualifiedByName = "getWarehouseDetails")
    ModelDetailsDTO toDTO(QueryModel queryModel);

    default WarehouseDetails getWarehouseDetails(Long warehouseId) {
        Warehouse warehouse = ObjectRegistry.getInstance(WarehouseService.class).getWarehouse(warehouseId, true);
        return WarehouseDetails.builder().id(warehouse.getId()).name(warehouse.getName())
                .type(warehouse.getType()).logoUrl(warehouse.getType().getLogoUrl()).build();
    }
}
