package io.castled.dtomappers;

import io.castled.dtos.PipelineDTO;
import io.castled.dtos.WarehouseDetails;
import io.castled.dtos.querymodel.ModelDetailsDTO;
import io.castled.models.QueryModel;
import io.castled.models.Warehouse;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
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

    public static ModelDetailsDTO toDetailedDTO(QueryModel queryModel, Warehouse warehouse , List<PipelineDTO> pipelineDTOs) {
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
        modelDetailsDTO.activeSyncDetails(pipelineDTOs);
        modelDetailsDTO.activeSyncsCount(CollectionUtils.isEmpty(pipelineDTOs)?0:pipelineDTOs.size());
        return modelDetailsDTO.build();
    }
}
