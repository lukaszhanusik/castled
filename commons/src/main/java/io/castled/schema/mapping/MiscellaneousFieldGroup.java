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

    private boolean autoMap;

    public MiscellaneousFieldGroup(boolean autoMap) {
        super("Which column would you like to sync as custom destination fields",
                "Configure how the columns in your query results are added as custom fields in your destination object",
                MappingGroupType.MISCELLANEOUS_FIELDS);
        this.setAutoMap(autoMap);
    }

    public MiscellaneousFieldGroup(String title, String description, MappingGroupType type) {
        super(title, description, type);
    }
}
