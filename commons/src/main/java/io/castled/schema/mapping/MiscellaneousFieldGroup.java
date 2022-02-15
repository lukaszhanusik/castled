package io.castled.schema.mapping;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class MiscellaneousFieldGroup extends MappingGroup {

    public MiscellaneousFieldGroup() {
        super("Which column would you like to sync as custom destination fields",
                "Configure how the columns in your query results are added as custom fields in your destination object",
                MappingGroupType.MISCELLANEOUS_FIELDS);
    }

    public MiscellaneousFieldGroup(String title, String description, MappingGroupType type) {
        super(title, description, type);
    }
}
