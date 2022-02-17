package io.castled.schema.mapping;

import io.castled.schema.SchemaFieldDTO;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class PrimaryKeyGroup extends MappingGroup {

    private List<SchemaFieldDTO> primaryKeys;

    public PrimaryKeyGroup() {
        super("How to match Source Record to Destination Object",
                "Identify a column in the source record which will help uniquely identify the destination object field",
                MappingGroupType.PRIMARY_KEYS);
    }

    public PrimaryKeyGroup(String title, String description, MappingGroupType type) {
        super(title, description, type);
    }
}
