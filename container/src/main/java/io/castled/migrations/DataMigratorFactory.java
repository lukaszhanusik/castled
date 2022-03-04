package io.castled.migrations;

import com.google.inject.Inject;
import lombok.Getter;

import java.util.Map;

@Getter
public class DataMigratorFactory {

    private final Map<MigrationType, DataMigrator> dataMigrators;

    @Inject
    public DataMigratorFactory(Map<MigrationType, DataMigrator> dataMigrators) {
        this.dataMigrators = dataMigrators;
    }

    public DataMigrator getDataMigrator(MigrationType migrationType) {
        return this.dataMigrators.get(migrationType);
    }
}
