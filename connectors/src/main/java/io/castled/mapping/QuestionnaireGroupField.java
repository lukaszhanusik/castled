package io.castled.mapping;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
public class QuestionnaireGroupField extends MappingGroupField {

    @Builder
    public QuestionnaireGroupField(String title, String description, boolean optional, String name) {
        super(name);
        this.title = title;
        this.description = description;
        this.optional = optional;

    }

    private String title;
    private String displayName;
    private String description;
    private boolean optional;
}
