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

import java.util.Map;
import java.util.Optional;

public class QueryModelDTOMapper {

    public static ModelDetailsDTO toDTO(QueryModel queryModel, Warehouse warehouse , Integer activeSyncsCount) {
        if ( queryModel == null ) {
            return null;
        }

        ModelDetailsDTO.ModelDetailsDTOBuilder modelDetailsDTO = ModelDetailsDTO.builder();
        modelDetailsDTO.warehouse(WarehouseDetails.builder().id(warehouse.getId()).name(warehouse.getName())
                .type(warehouse.getType()).logoUrl(warehouse.getType().getLogoUrl()).build());
        modelDetailsDTO.id( queryModel.getId() );
        modelDetailsDTO.userId( queryModel.getUserId() );
        modelDetailsDTO.teamId( queryModel.getTeamId() );
        modelDetailsDTO.modelName( queryModel.getModelName() );
        modelDetailsDTO.modelType( queryModel.getModelType() );
        modelDetailsDTO.modelDetails( queryModel.getModelDetails() );
        modelDetailsDTO.queryModelPK( queryModel.getQueryModelPK() );
        modelDetailsDTO.activeSyncsCount(Optional.ofNullable(activeSyncsCount).orElse(0));
        return modelDetailsDTO.build();
    }
}
