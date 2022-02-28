package io.castled.schema.mapping;

import io.castled.schema.SchemaFieldDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PrimaryKeyFieldsGroup extends MappingGroup {

    private List<SchemaFieldDTO> primaryKeys;

    public PrimaryKeyFieldsGroup() {
        super("How to match Source Record to Destination Object",
                "Identify a column in the source record which will help uniquely identify the destination object field",
                MappingGroupType.PRIMARY_KEYS);
    }

    public PrimaryKeyFieldsGroup(String title, String description, MappingGroupType type) {
        super(title, description, type);
    }
}
