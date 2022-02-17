package io.castled.schema.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MappingGroup {
    private String title;
    private String description;
    private MappingGroupType type;
}
