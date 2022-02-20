package io.castled.migrations;

import io.castled.models.FieldMapping;
import lombok.Data;

import java.util.List;

@Data
public class OldMappingConfig {

    private List<FieldMapping> fieldMappings;
    private List<String> primaryKeys;
}
