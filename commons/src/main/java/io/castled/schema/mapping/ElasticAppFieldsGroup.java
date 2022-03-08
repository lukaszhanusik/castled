package io.castled.schema.mapping;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ElasticAppFieldsGroup extends MappingGroup {

    private boolean autoMap;
    private boolean pkRequired;

    public ElasticAppFieldsGroup(boolean autoMap, boolean pkRequired) {
        super("Which column would you like to sync as custom destination fields",
                "Configure how the columns in your query results are added as custom fields in your destination object",
                MappingGroupType.MISCELLANEOUS_FIELDS);
        this.setAutoMap(autoMap);
        this.setPkRequired(pkRequired);
    }

    public ElasticAppFieldsGroup(String title, String description, MappingGroupType type) {
        super(title, description, type);
    }
}
