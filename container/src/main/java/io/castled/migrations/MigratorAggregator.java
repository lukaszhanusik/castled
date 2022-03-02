package io.castled.migrations;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
public class MigratorAggregator {

    private final Map<MigrationType,DataMigrator> migratorList;

    public MigratorAggregator(){
        this.migratorList = Maps.newHashMap();
        this.migratorList.put(MigrationType.MAPPING_MIGRATION,new MappingDataMigrator());
    }
}
