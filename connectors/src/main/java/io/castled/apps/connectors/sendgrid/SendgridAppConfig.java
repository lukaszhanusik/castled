package io.castled.apps.connectors.sendgrid;

import io.castled.apps.BaseAppConfig;
import io.castled.forms.FormField;
import io.castled.forms.FormFieldSchema;
import io.castled.forms.FormFieldType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendgridAppConfig extends BaseAppConfig {

    @FormField(description = "Sendgrid API Key", title = "API Key", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String apiKey;
}
