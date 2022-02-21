package io.castled.resources;

import com.google.inject.Singleton;
import io.castled.dtos.EntityCreateResponse;
import io.castled.dtos.PipelineConfigDTO;

import io.castled.resources.models.BulkEmployeeDTO;
import io.castled.resources.models.NestedBulkEmployeeDTO;
import io.castled.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.ws.rs.*;
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
    public EntityCreateResponse updateEmployee(Employee employee){
        log.info("Received put employee" + JsonUtils.objectToString(employee));
        return new EntityCreateResponse(employee.getId());
    }

    @DELETE
    @Path("/employees")
    public EntityCreateResponse deleteEmployee(Employee employee) {
        log.info("Received post employee" + JsonUtils.objectToString(employee));
        return new EntityCreateResponse(employee.getId());
    }


    @POST
    @Path("/employees/bulk")
    public void createEmployee(BulkEmployeeDTO employeeList) {
        log.info("Received post bulk employees" + JsonUtils.objectToString(employeeList));
    }


    @POST
    @Path("/employees/nested")
    public void createEmployee(NestedBulkEmployeeDTO employeeList) {
        log.info("Received post bulk employees" + JsonUtils.objectToString(employeeList));
    }
}
