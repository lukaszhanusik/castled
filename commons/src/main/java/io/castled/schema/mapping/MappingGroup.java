package io.castled.schema.mapping;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public abstract class MappingGroup {
    private String title;
    private String description;
    private MappingGroupType type;

}
