package io.castled.apps.connectors.mixpanel;

import io.castled.apps.BaseAppConfig;
import io.castled.forms.FormField;
import io.castled.forms.FormFieldSchema;
import io.castled.forms.FormFieldType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MixpanelAppConfig extends BaseAppConfig {

    @FormField(description = "Project token is provided as a value inside of the data sent to mixpanel",
            title = "Project Token", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String projectToken;

    @FormField(description = "Project API Secret is required for ingesting any events with a timestamp of more than five days",
            title = "API Secret", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String apiSecret;
}
