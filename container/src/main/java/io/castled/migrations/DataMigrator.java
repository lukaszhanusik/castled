package io.castled.migrations;

public interface DataMigrator {

    MigrationType getMigrationType();

    void migrateData();
}
