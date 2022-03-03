package io.castled.migrations;

import io.castled.ObjectRegistry;
import io.castled.migrations.models.MigrationDetails;
import org.jdbi.v3.core.Jdbi;

public abstract class AbstractDataMigrator implements DataMigrator {

    @Override
    public void runDataMigration() {
        MigrationDetailsDAO migrationsDAO = ObjectRegistry.getInstance(Jdbi.class).onDemand(MigrationDetailsDAO.class);
        MigrationDetails migrationDetails = migrationsDAO.getMigrationDetails(getMigrationType());
        if (isMigrationPending(migrationDetails)) {
            try {
                createMigrationDetail(migrationDetails);
                migrateData();
                updateStatus(MigrationStatus.SUCCESS, "migration successfully completed");
            } catch (Exception ex) {
                updateStatus(MigrationStatus.FAILURE, "Migration failed :" + ex.getMessage());
            }
        }
    }

    abstract void migrateData();

    private Long createMigrationDetail(MigrationDetails migrationDetails) {
        if (migrationDetails == null) {
            MigrationDetailsDAO migrationsDAO = ObjectRegistry.getInstance(Jdbi.class).onDemand(MigrationDetailsDAO.class);
            return migrationsDAO.createMigrationDetails(getMigrationType());
        }
        return migrationDetails.getId();
    }

    private boolean isMigrationPending(MigrationDetails migrationDetails) {
        if (migrationDetails == null || (migrationDetails != null && migrationDetails.getStatus().equals(MigrationStatus.FAILURE))) {
            return true;
        }
        return false;
    }

    private void updateStatus(MigrationStatus status, String details) {
        MigrationDetailsDAO migrationsDAO = ObjectRegistry.getInstance(Jdbi.class).onDemand(MigrationDetailsDAO.class);
        migrationsDAO.updateMigrationStatus(getMigrationType(), status, details);
    }
}
