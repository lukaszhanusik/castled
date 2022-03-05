package io.castled.mapping;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FixedGroupAppField extends MappingGroupField {

    @Builder
    public FixedGroupAppField(String name, String displayName, boolean optional) {
        super(name);
        this.optional = optional;
        this.displayName = displayName;
    }

    private boolean optional;
    private String displayName;
}
