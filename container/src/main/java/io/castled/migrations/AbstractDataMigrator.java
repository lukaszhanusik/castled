package io.castled.migrations;

import io.castled.ObjectRegistry;
import io.castled.migrations.models.DataMigration;
import lombok.Getter;
import org.jdbi.v3.core.Jdbi;

import java.util.Optional;

public abstract class AbstractDataMigrator implements DataMigrator {

    @Getter
    private final MigrationType migrationType;

    public AbstractDataMigrator(MigrationType migrationType) {
        this.migrationType = migrationType;
    }

    @Override
    public void migrateData() {
        DataMigrationsDAO migrationsDAO = ObjectRegistry.getInstance(Jdbi.class).onDemand(DataMigrationsDAO.class);
        DataMigration dataMigration = migrationsDAO.getDataMigration(getMigrationType());
        if (Optional.ofNullable(dataMigration).filter(DataMigration::isCompleted).isPresent()) {
            return;
        }
        if (dataMigration == null) {
            migrationsDAO.createDataMigration(getMigrationType());
        }
        try {
            doMigrateData();
            migrationsDAO.markMigrationProcessed(getMigrationType());
        } catch (Exception e) {
            migrationsDAO.markMigrationFailed(getMigrationType(), e.getMessage());
        }
    }

    abstract void doMigrateData();
}
