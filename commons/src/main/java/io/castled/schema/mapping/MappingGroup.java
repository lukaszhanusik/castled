package io.castled.schema.mapping;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public class MappingGroup {
    private String title;
    private String description;
    private MappingGroupType type;

}
