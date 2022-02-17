package io.castled.schema.mapping;

import io.castled.schema.SchemaFieldDTO;
import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
public class DestinationFieldGroup extends MappingGroup {

    private List<SchemaFieldDTO> mandatoryFields;
    private List<SchemaFieldDTO> optionalFields;

    public DestinationFieldGroup() {
        super("", "", MappingGroupType.DESTINATION_FIELDS);
    }
}
