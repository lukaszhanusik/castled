package io.castled.apps.connectors.restapi;

import io.castled.apps.BaseAppConfig;
import io.castled.forms.FormField;
import io.castled.forms.FormFieldSchema;
import io.castled.forms.FormFieldType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestApiAppConfig extends BaseAppConfig {

    @FormField(description = "API URL", title = "API URL", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String apiURL;

    @FormField(description = "API Key", title = "API Key", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String apiKey;
}
