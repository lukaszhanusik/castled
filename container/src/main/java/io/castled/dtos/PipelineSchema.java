package io.castled.dtos;

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

    public PipelineSchema(SimpleSchema warehouseSchema ,List<MappingGroup> mappingGroups){
        this.warehouseSchema = warehouseSchema;
        this.mappingGroups = mappingGroups;
    }
}

