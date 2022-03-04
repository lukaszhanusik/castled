package io.castled.commands;

import com.mysql.cj.jdbc.MysqlDataSource;
import io.castled.CastledApplication;
import io.castled.CastledConfiguration;
import io.castled.CastledStateStore;
import io.castled.ObjectRegistry;
import io.castled.daos.InstallationDAO;
import io.castled.events.CastledEventsClient;
import io.castled.events.NewInstallationEvent;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.migrations.DataMigrator;
import io.castled.migrations.MigrationType;
import io.castled.migrations.DataMigratorFactory;
import io.castled.services.UsersService;
import io.castled.utils.AsciiArtUtils;
import io.castled.utils.FileUtils;
import io.dropwizard.cli.ServerCommand;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.argparse4j.inf.Namespace;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class CastledServerCommand extends ServerCommand<CastledConfiguration> {

    public CastledServerCommand(CastledApplication castledApplication) {
        super(castledApplication, "castled-server", "Runs the castled server");
    }

    protected void run(Environment environment, Namespace namespace, CastledConfiguration configuration) throws Exception {
        runMigrations(configuration);
        runCodeLevelMigrations();
        super.run(environment, namespace, configuration);
        AsciiArtUtils.drawCastled();

    }

    private void runMigrations(CastledConfiguration configuration) {
        Flyway flyway = new Flyway();
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL(configuration.getDatabase().getUrl());
        flyway.setDataSource(mysqlDataSource);
        flyway.setLocations("migration");
        flyway.migrate();

        //create test team and user if required
        UsersService usersService = ObjectRegistry.getInstance(UsersService.class);
        if (usersService.getUser() == null) {
            usersService.createTestTeamAndUser();
        }
        createNewInstallationIfRequired();

    }

    private void runCodeLevelMigrations() {
        try {
            DataMigratorFactory dataMigratorFactory = ObjectRegistry.getInstance(DataMigratorFactory.class);
            List<MigrationType> allMigrations = Objects.requireNonNull(FileUtils.getResourceFileLines("java_migrations"))
                    .stream().map(MigrationType::valueOf).collect(Collectors.toList());
            for (MigrationType migrationType : allMigrations) {
                dataMigratorFactory.getDataMigrator(migrationType).migrateData();
            }
        } catch (IOException e) {
            log.error("Code level data migration failed", e);
            throw new CastledRuntimeException(e.getMessage());
        }
    }

    private void createNewInstallationIfRequired() {
        InstallationDAO installationDAO = ObjectRegistry.getInstance(Jdbi.class).onDemand(InstallationDAO.class);
        String installId = installationDAO.getInstallation();
        if (installId != null) {
            CastledStateStore.installId = installId;
            return;
        }
        String newInstallationId = UUID.randomUUID().toString();
        installationDAO.createInstallation(newInstallationId);
        CastledStateStore.installId = newInstallationId;
        ObjectRegistry.getInstance(CastledEventsClient.class).publishCastledEvent(new NewInstallationEvent(newInstallationId));
    }
}
