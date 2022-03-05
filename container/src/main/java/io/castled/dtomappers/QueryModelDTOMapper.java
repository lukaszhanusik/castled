package io.castled.dtomappers;

import io.castled.dtos.PipelineDTO;
import io.castled.dtos.WarehouseDetails;
import io.castled.dtos.querymodel.QueryModelDTO;
import io.castled.models.QueryModel;
import io.castled.models.Warehouse;
import io.castled.schema.models.FieldSchema;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Optional;

public class QueryModelDTOMapper {

    public static QueryModelDTO toDTO(QueryModel queryModel, Warehouse warehouse, Integer activeSyncsCount) {
        if (queryModel == null) {
            return null;
        }

        QueryModelDTO.QueryModelDTOBuilder queryModelDTOBuilder = QueryModelDTO.builder();
        queryModelDTOBuilder.warehouse(WarehouseDetails.builder().id(warehouse.getId()).name(warehouse.getName())
                .type(warehouse.getType()).logoUrl(warehouse.getType().getLogoUrl()).build());
        queryModelDTOBuilder.id(queryModel.getId());
        queryModelDTOBuilder.userId(queryModel.getUserId());
        queryModelDTOBuilder.teamId(queryModel.getTeamId());
        queryModelDTOBuilder.name(queryModel.getName());
        queryModelDTOBuilder.type(queryModel.getType());
        queryModelDTOBuilder.details(queryModel.getDetails());
        queryModelDTOBuilder.queryPK(queryModel.getQueryPK());
        queryModelDTOBuilder.activeSyncsCount(Optional.ofNullable(activeSyncsCount).orElse(0));
        queryModelDTOBuilder.demo(queryModel.isDemo());
        return queryModelDTOBuilder.build();
    }

    public static QueryModelDTO toDetailedDTO(QueryModel queryModel, Warehouse warehouse, List<FieldSchema> warehouseFieldSchemas, List<PipelineDTO> pipelineDTOs) {
        if (queryModel == null) {
            return null;
        }

        QueryModelDTO.QueryModelDTOBuilder queryModelDTOBuilder = QueryModelDTO.builder();
        queryModelDTOBuilder.warehouse(WarehouseDetails.builder().id(warehouse.getId()).name(warehouse.getName())
                .type(warehouse.getType()).logoUrl(warehouse.getType().getLogoUrl()).build());
        queryModelDTOBuilder.columnDetails(warehouseFieldSchemas);
        queryModelDTOBuilder.id(queryModel.getId());
        queryModelDTOBuilder.userId(queryModel.getUserId());
        queryModelDTOBuilder.teamId(queryModel.getTeamId());
        queryModelDTOBuilder.name(queryModel.getName());
        queryModelDTOBuilder.type(queryModel.getType());
        queryModelDTOBuilder.details(queryModel.getDetails());
        queryModelDTOBuilder.queryPK(queryModel.getQueryPK());
        queryModelDTOBuilder.activeSyncDetails(pipelineDTOs);
        queryModelDTOBuilder.activeSyncsCount(CollectionUtils.isEmpty(pipelineDTOs) ? 0 : pipelineDTOs.size());
        queryModelDTOBuilder.demo(queryModel.isDemo());
        return queryModelDTOBuilder.build();
    }
}
