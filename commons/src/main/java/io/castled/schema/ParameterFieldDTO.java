package io.castled.schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParameterFieldDTO extends SchemaFieldDTO {
    private String title;
    private String description;


    public ParameterFieldDTO(String title, String description, String fieldName, String fieldDisplayName, String type, boolean optional) {
        super(fieldName, fieldDisplayName, type, optional);
        this.title = title;
        this.description = description;
    }
}
