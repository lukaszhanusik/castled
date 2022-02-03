package io.castled.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class TargetFieldsMapping extends CastledDataMapping {

    private List<FieldMapping> fieldMappings;

    public Map<String, String> getMappingForAppFields(List<String> appFields) {
        return fieldMappings.stream().filter(fieldMapping -> appFields.contains(fieldMapping.getAppField()))
                .collect(Collectors.toMap(FieldMapping::getAppField, FieldMapping::getWarehouseField));
    }

    public Map<String, String> appWarehouseMapping() {
        return fieldMappings.stream().filter(fieldMapping -> !fieldMapping.isSkipped())
                .collect(Collectors.toMap(FieldMapping::getAppField, FieldMapping::getWarehouseField));
    }

    public Map<String, String> warehouseAppMapping() {
        return fieldMappings.stream().filter(fieldMapping -> !fieldMapping.isSkipped())
                .collect(Collectors.toMap(FieldMapping::getWarehouseField, FieldMapping::getAppField));
    }

    public void addAdditionalMappings(List<FieldMapping> additionalMappings) {
        fieldMappings.addAll(additionalMappings);
    }
}
