package io.castled.schema.mapping;

import io.castled.schema.SchemaFieldDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FixedAppFieldsGroup extends MappingGroup {

    private List<SchemaFieldDTO> mandatoryFields;
    private List<SchemaFieldDTO> optionalFields;

    public FixedAppFieldsGroup() {
        super("Which column would you like to sync to destination fields",
                "Configure how the columns in your query results should be mapped to fields in your destination",
                MappingGroupType.DESTINATION_FIELDS);
    }
}
