package io.castled.services;


import com.google.api.client.util.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.castled.daos.QueryModelDAO;
import io.castled.dtomappers.PipelineDTOMapper;
import io.castled.dtomappers.QueryModelDTOMapper;
import io.castled.dtos.PipelineDTO;
import io.castled.dtos.querymodel.*;
import io.castled.models.ModelAggregate;
import io.castled.models.Pipeline;
import io.castled.models.QueryModel;
import io.castled.models.Warehouse;
import io.castled.models.users.User;
import io.castled.resources.validators.ResourceAccessController;
import io.castled.schema.models.FieldSchema;
import io.castled.warehouses.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class QueryModelService {

    final private QueryModelDAO queryModelDAO;
    final private ResourceAccessController resourceAccessController;
    final private WarehouseService warehouseService;
    final private PipelineService pipelineService;

    @Inject
    public QueryModelService(Jdbi jdbi, ResourceAccessController resourceAccessController,
                             WarehouseService warehouseService, PipelineService pipelineService) {
        this.queryModelDAO = jdbi.onDemand(QueryModelDAO.class);
        this.resourceAccessController = resourceAccessController;
        this.warehouseService = warehouseService;
        this.pipelineService = pipelineService;
    }

    public Long createModel(ModelInputDTO modelInputDTO, User user) {
        QueryModel queryModel = this.queryModelDAO.getQueryModelByModelName(modelInputDTO.getModelName());
        if (queryModel != null) {
            throw new BadRequestException("Model name already taken!. Enter another model name");
        }
        return this.queryModelDAO.createModel(modelInputDTO, user);
    }

    public Long createQueryModel(ModelInputDTO modelInputDTO, Long teamId) {
        QueryModel queryModel = this.queryModelDAO.getQueryModelByModelName(modelInputDTO.getModelName());
        if (queryModel != null) {
            throw new BadRequestException("Model name already taken!. Enter another model name");
        }
        return this.queryModelDAO.createQueryModel(modelInputDTO, teamId);
    }

    public ModelDetailsDTO getQueryModel(Long modelId, Long teamId) {
        QueryModel queryModel = this.queryModelDAO.getQueryModel(modelId);
        this.resourceAccessController.validateQueryModelAccess(queryModel, teamId);
        return convertToModelDetailsDTO(teamId, queryModel);
    }

    public QueryModel getQueryModel(Long modelId) {
        return this.queryModelDAO.getQueryModel(modelId);
    }

    public String getSourceQuery(Long modelId) {
        QueryModel queryModel = this.queryModelDAO.getQueryModel(modelId);
        return getSourceQuery(queryModel);
    }

    private String getSourceQuery(QueryModel queryModel) {
        QueryModelDetails modelDetails = queryModel.getModelDetails();
        if (modelDetails instanceof SqlQueryModelDetails) {
            return ((SqlQueryModelDetails) queryModel.getModelDetails()).getSourceQuery();
        } else if (modelDetails instanceof TableQueryModelDetails) {
            return ((TableQueryModelDetails) queryModel.getModelDetails()).getSourceQuery();
        }
        return null;
    }

    public List<String> getQueryModelPrimaryKeys(Long modelId) {
        return this.queryModelDAO.getQueryModel(modelId).getQueryModelPK().getPrimaryKeys();
    }

    public void deleteModel(Long id, Long teamId) {
        this.resourceAccessController.validateQueryModelAccess(getQueryModel(id), teamId);
        this.queryModelDAO.deleteModel(id);
    }

    public List<ModelDetailsDTO> getAllModels(Long warehouseId, Long teamId) {
        List<QueryModel> queryModels = null;
        if (warehouseId != null) {
            queryModels = this.queryModelDAO.getQueryModelsByWarehouseAndTeam(warehouseId, teamId);
        } else {
            queryModels = this.queryModelDAO.getQueryModelsByTeam(teamId);
        }
        return convertToModelDetailDTOList(teamId, queryModels);
    }

    private List<ModelDetailsDTO> convertToModelDetailDTOList(Long teamId, List<QueryModel> queryModels) {
        Map<Long, Warehouse> warehouseMap = prepareWarehouseMap(queryModels);
        Map<Long, Integer> modelToSyncCountMap = prepareModelToSyncCountMap(teamId, queryModels);
        List<ModelDetailsDTO> modelDetailsDTOS = Lists.newArrayList();
        queryModels.forEach(queryModel -> modelDetailsDTOS.add(QueryModelDTOMapper.toDTO(queryModel,
                warehouseMap.get(queryModel.getWarehouseId()), modelToSyncCountMap.get(queryModel.getId()))));
        return modelDetailsDTOS;
    }

    private ModelDetailsDTO convertToModelDetailsDTO(Long teamId, QueryModel queryModel) {
        Warehouse warehouse = warehouseService.getWarehouse(queryModel.getWarehouseId());
        List<FieldSchema> fieldSchemas = null;
        QueryModelDetails modelDetails = queryModel.getModelDetails();
        if (modelDetails instanceof SqlQueryModelDetails) {
            SqlQueryModelDetails sqlQueryModelDetails = (SqlQueryModelDetails) modelDetails;
            try {
                fieldSchemas = warehouseService.fetchSchema(warehouse.getId(), sqlQueryModelDetails.getSourceQuery()).getFieldSchemas();
            } catch (Exception e) {
                log.error("Warehouse column details fetch failed {}",e.getMessage());
            }
        }
        List<Pipeline> pipelines = pipelineService.listPipelinesByModelId(teamId, queryModel.getId());
        List<PipelineDTO> pipelineDTOS = pipelines.stream().map(PipelineDTOMapper.INSTANCE::toDetailedDTO).collect(Collectors.toList());
        return QueryModelDTOMapper.toDetailedDTO(queryModel, warehouse, fieldSchemas, pipelineDTOS);
    }

    private Map<Long, Warehouse> prepareWarehouseMap(List<QueryModel> queryModels) {
        Set<Long> warehouseList = queryModels.stream().map(QueryModel::getWarehouseId).collect(Collectors.toSet());
        List<Warehouse> warehouses = warehouseService.getWarehouses(Lists.newArrayList(warehouseList), true);
        return warehouses.stream().collect(Collectors.toMap(Warehouse::getId, Function.identity()));
    }

    private Map<Long, Integer> prepareModelToSyncCountMap(Long teamId, List<QueryModel> queryModels) {
        Set<Long> modelIdList = queryModels.stream().map(QueryModel::getId).collect(Collectors.toSet());
        List<ModelAggregate> modelAggregates = pipelineService.getModelAggregates(teamId, Lists.newArrayList(modelIdList));
        return modelAggregates.stream().collect(Collectors.toMap(ModelAggregate::getModelId, ModelAggregate::getPipelines));
    }
}
