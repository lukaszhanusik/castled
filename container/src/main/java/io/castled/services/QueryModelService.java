package io.castled.services;


import com.google.api.client.util.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.castled.daos.QueryModelDAO;
import io.castled.dtos.querymodel.QueryModelDTO;
import io.castled.dtos.querymodel.SqlQueryModelDetails;
import io.castled.dtos.querymodel.TableQueryModelDetails;
import io.castled.models.QueryModel;
import io.castled.models.users.User;
import io.castled.resources.validators.ResourceAccessController;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.BadRequestException;
import java.util.List;

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

    public Long createModel(QueryModelDTO queryModelDTO, User user) {
        QueryModel queryModel = this.queryModelDAO.getQueryModelByModelName(queryModelDTO.getModelName());
        if (queryModel != null) {
            throw new BadRequestException("Model name already taken,enter another model name");
        }
        Long modelId = this.queryModelDAO.createModel(queryModelDTO, user);
        return modelId;
    }

    public QueryModel getQueryModel(Long modelId) {
        return this.queryModelDAO.getQueryModel(modelId);
    }

    public String getSourceQuery(Long modelId) {
        QueryModel queryModel = this.queryModelDAO.getQueryModel(modelId);
        return getSourceQuery(queryModel);
    }

    private String getSourceQuery(QueryModel queryModel) {

        switch (queryModel.getModelType()) {
            case SQL_QUERY_EDITOR:
                ((SqlQueryModelDetails) queryModel.getModelDetails()).getSourceQuery();
                break;
            case TABLE_SELECTOR:
                ((TableQueryModelDetails) queryModel.getModelDetails()).getSourceQuery();

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

    public List<QueryModelDTO> getAllModels(Long warehouseId, Long teamId) {
        List<QueryModel> queryModels = null;
        if(warehouseId!=null){
            queryModels = this.queryModelDAO.getQueryModelsByWarehouseAndTeam(warehouseId, teamId);
        }
        else{
            queryModels = this.queryModelDAO.getQueryModelsByTeam(teamId);
        }
        return mapEntityToDTO(queryModels);
    }

    private List<QueryModelDTO> mapEntityToDTO(List<QueryModel> queryModels) {
        List<QueryModelDTO> queryModelDTOs = Lists.newArrayList();

        return queryModelDTOs;
    }

    private QueryModelDTO mapEntityToDTO(QueryModel queryModel) {
        QueryModelDTO queryModelDTO = new QueryModelDTO();

        return queryModelDTO;
    }
}
