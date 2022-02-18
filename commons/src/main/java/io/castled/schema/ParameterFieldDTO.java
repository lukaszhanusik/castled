package io.castled.schema;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ParameterFieldDTO extends SchemaFieldDTO{
    private String title;
    private String description;

    public ParameterFieldDTO(String title , String description,String fieldName , String type,boolean optional){
        super(fieldName,type,optional);
        this.title = title;
        this.description = description;
    }
}
