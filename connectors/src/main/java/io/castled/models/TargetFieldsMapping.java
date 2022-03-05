package io.castled.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TargetFieldsMapping extends CastledDataMapping {

    private List<String> primaryKeys;
    private List<FieldMapping> fieldMappings;
}
