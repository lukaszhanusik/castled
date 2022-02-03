package io.castled.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TargetTemplateMapping extends CastledDataMapping {

    private RestMethod method;
    private String url;
    private String template;
    private Map<String, String> headers;
}
