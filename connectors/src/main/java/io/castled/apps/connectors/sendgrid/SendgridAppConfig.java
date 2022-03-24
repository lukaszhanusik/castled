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

    @FormField(description = "API key to access endpoints for updating user contacts", title = "API Key", schema = FormFieldSchema.STRING, type = FormFieldType.PASSWORD)
    private String apiKey;
}