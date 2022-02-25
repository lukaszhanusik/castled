package io.castled.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AppFieldDetails {

    private String title;
    private String description;
    private String internalName;
    private String displayName;
    private String type;
    private boolean optional;
    private boolean custom;
}
