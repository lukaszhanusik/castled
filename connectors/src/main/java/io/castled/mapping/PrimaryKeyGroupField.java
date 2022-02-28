package io.castled.mapping;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class PrimaryKeyGroupField extends MappingGroupField {

    @Builder
    public PrimaryKeyGroupField(String name, String displayName, boolean optional) {
        super(name, displayName);
        this.optional = optional;
    }

    private boolean optional;
}
