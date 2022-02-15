package io.castled.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.castled.apps.models.PrimaryKeyEligibles;
import io.castled.schema.SimpleSchema;
import io.castled.schema.mapping.MappingGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PipelineSchema {
    private SimpleSchema warehouseSchema;
    private List<MappingGroup> mappingGroups;

    private SimpleSchema appSchema;
    private PrimaryKeyEligibles pkEligibles;

    public PipelineSchema(SimpleSchema warehouseSchema ,SimpleSchema appSchema,PrimaryKeyEligibles pkEligibles){
        this.warehouseSchema = warehouseSchema;
        this.appSchema = appSchema;
        this.pkEligibles = pkEligibles;
    }

    public PipelineSchema(SimpleSchema warehouseSchema ,List<MappingGroup> mappingGroups){
        this.warehouseSchema = warehouseSchema;
        this.mappingGroups = mappingGroups;
    }
}

