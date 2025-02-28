package io.castled.apps.connectors.customerio;

import io.castled.apps.BaseAppConfig;
import io.castled.forms.FormField;
import io.castled.forms.FormFieldSchema;
import io.castled.forms.FormFieldType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerIOAppConfig extends BaseAppConfig {

    @FormField(description = "Site ID generated for the workspace you are creating in customer.io",
            title = "Site ID", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String siteId;

    @FormField(description = "Tracking API Key generated for the workspace you are creating in customer.io",
            title = "API Key", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String apiKey;
}
