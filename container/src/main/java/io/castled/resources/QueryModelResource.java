package io.castled.resources;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.castled.dtos.EntityCreateResponse;
import io.castled.dtos.querymodel.ModelDetailsDTO;
import io.castled.dtos.querymodel.ModelInputDTO;
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
    public EntityCreateResponse createModel(@Valid ModelInputDTO modelInputDTO, @Auth User user) {
        return new EntityCreateResponse(this.queryModelService.createModel(modelInputDTO, user));
    }

    @GET
    @Path("/{id}")
    public ModelDetailsDTO getModel(@PathParam("id") Long id, @Auth User user) {
        return this.queryModelService.getQueryModel(id, user.getTeamId());
    }

    @DELETE
    @Path("/{id}")
    public void deleteModel(@PathParam("id") Long id, @Auth User user) {
        this.queryModelService.deleteModel(id, user.getTeamId());
    }

    @GET
    public List<ModelDetailsDTO> getAllModels(@QueryParam("warehouseId") Long warehouseId, @Auth User user) {
        return this.queryModelService.getAllModels(warehouseId, user.getTeamId());
    }
}
