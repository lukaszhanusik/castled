package io.castled.schema.mapping;

import io.castled.schema.ParameterFieldDTO;
import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
public class ImportantParameterGroup extends MappingGroup{

    private List<ParameterFieldDTO> fields;

    public ImportantParameterGroup(){
        super("","",MappingGroupType.IMPORTANT_PARAMS);
    }

    public ImportantParameterGroup(String title, String description, MappingGroupType type) {
        super(title, description, type);
    }
}
