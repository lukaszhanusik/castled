package io.castled.migrations;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.castled.models.users.User;
import io.dropwizard.auth.Auth;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1/migrations")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
@Slf4j
public class MigrationsResource {

    private final MigrationsService migrationsService;

    @Inject
    public MigrationsResource(MigrationsService migrationsService) {
        this.migrationsService = migrationsService;
    }

    @Path("/data-mapping")
    @POST
    public void migrateDataMapping(@Auth User user) {
        this.migrationsService.migrateDataMapping();
    }
}
