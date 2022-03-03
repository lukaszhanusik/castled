package io.castled.migrations;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

@Getter
public class DataMigratorAggregator {

    private final Map<MigrationType, DataMigrator> dataMigratorMap;

    public DataMigratorAggregator() {
        this.dataMigratorMap = Maps.newHashMap();
        this.dataMigratorMap.put(MigrationType.MAPPING_MIGRATION, new MappingDataMigrator());
    }
}
