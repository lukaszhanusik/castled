package io.castled.migrations;

public interface DataMigrator {

    MigrationType getMigrationType();

    MigrationResult migrateData();
}
