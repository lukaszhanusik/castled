package io.castled.resources;

import com.google.inject.Singleton;
import io.castled.dtos.EntityCreateResponse;
import io.castled.dtos.PipelineConfigDTO;

import io.castled.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1/test")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
@Slf4j
public class TestResource {

    @POST
    @Path("/employees")
    public EntityCreateResponse createEmployee(Employee employee) {
        log.info("Received post employee" + JsonUtils.objectToString(employee));
        return new EntityCreateResponse(employee.getId());
    }

    @PUT
    @Path("/employees")
    public EntityCreateResponse updateEmployee(Employee employee) {
        log.info("Received put employee" + JsonUtils.objectToString(employee));
        return new EntityCreateResponse(employee.getId());
    }
}
