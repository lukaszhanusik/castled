package io.castled.resources;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.castled.dtos.EntityCreateResponse;
import io.castled.dtos.querymodel.QueryModelDTO;
import io.castled.models.QueryModel;
import io.castled.models.users.User;
import io.castled.resources.validators.ResourceAccessController;
import io.castled.services.QueryModelService;
import io.dropwizard.auth.Auth;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/v1/models")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
@Slf4j
public class QueryModelResource {

    private final QueryModelService queryModelService;
    private final ResourceAccessController resourceAccessController;

    @Inject
    public QueryModelResource(QueryModelService queryModelService, ResourceAccessController resourceAccessController) {
        this.queryModelService = queryModelService;
        this.resourceAccessController = resourceAccessController;
    }

    @POST
    public EntityCreateResponse createModel(@Valid QueryModelDTO queryModelDTO,
                                            @Auth User user) {
        return new EntityCreateResponse(this.queryModelService.createModel(queryModelDTO, user));
    }

    @GET
    @Path("/{id}")
    public QueryModelDTO getModel(@PathParam("id") Long id, @Auth User user) {
        QueryModel queryModel = this.queryModelService.getQueryModel(id);
        this.resourceAccessController.validateQueryModelAccess(queryModel, user.getTeamId());
        return null;
    }

    @DELETE
    @Path("/{id}")
    public void deleteModel(@PathParam("id") Long id, @Auth User user) {
        this.queryModelService.deleteModel(id, user.getTeamId());
    }

    @GET
    public List<QueryModelDTO> getModelsByWarehouse(@QueryParam("warehouseId") Long warehouseId, @Auth User user) {
        return this.queryModelService.getModelsByWarehouse(warehouseId, user.getTeamId());
    }
}
