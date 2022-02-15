package io.castled.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParameterFieldDTO extends SchemaFieldDTO{
    private String title;
    private String description;

    private String fieldName;
    private String type;
    private boolean optional;

    public ParameterFieldDTO(String title , String description,String fieldName , String type,boolean optional){
        super(fieldName,type,optional);
        this.title = title;
        this.description = description;
    }
}
