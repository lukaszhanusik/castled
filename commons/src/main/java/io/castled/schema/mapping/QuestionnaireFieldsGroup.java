package io.castled.schema.mapping;

import io.castled.schema.ParameterFieldDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class QuestionnaireFieldsGroup extends MappingGroup {

    private List<ParameterFieldDTO> fields;

    public QuestionnaireFieldsGroup(){
        super("","",MappingGroupType.IMPORTANT_PARAMS);
    }

    public QuestionnaireFieldsGroup(String title, String description, MappingGroupType type) {
        super(title, description, type);
    }
}
