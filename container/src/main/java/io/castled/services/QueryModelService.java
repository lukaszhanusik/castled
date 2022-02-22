package io.castled.services;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.castled.daos.QueryModelDAO;
import io.castled.dtomappers.QueryModelDTOMapper;
import io.castled.dtos.querymodel.*;
import io.castled.models.QueryModel;
import io.castled.models.users.User;
import io.castled.resources.validators.ResourceAccessController;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class QueryModelService {

    final private QueryModelDAO queryModelDAO;
    final private ResourceAccessController resourceAccessController;

    @Inject
    public QueryModelService(Jdbi jdbi, ResourceAccessController resourceAccessController) {
        this.queryModelDAO = jdbi.onDemand(QueryModelDAO.class);
        this.resourceAccessController = resourceAccessController;
    }

    public Long createModel(ModelInputDTO modelInputDTO, User user) {
        QueryModel queryModel = this.queryModelDAO.getQueryModelByModelName(modelInputDTO.getModelName());
        if (queryModel != null) {
            throw new BadRequestException("Model name already taken,enter another model name");
        }
        Long modelId = this.queryModelDAO.createModel(modelInputDTO, user);
        return modelId;
    }

    public ModelDetailsDTO getQueryModel(Long modelId, Long teamId) {
        QueryModel queryModel = this.queryModelDAO.getQueryModel(modelId);
        this.resourceAccessController.validateQueryModelAccess(queryModel, teamId);
        return QueryModelDTOMapper.INSTANCE.toDTO(queryModel);
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
        return queryModels.stream()
                .map(QueryModelDTOMapper.INSTANCE::toDTO).collect(Collectors.toList());
    }
}
