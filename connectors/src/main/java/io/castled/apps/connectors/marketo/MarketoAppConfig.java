package io.castled.apps.connectors.marketo;

import io.castled.apps.BaseAppConfig;
import io.castled.forms.FormField;
import io.castled.forms.FormFieldSchema;
import io.castled.forms.FormFieldType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarketoAppConfig extends BaseAppConfig {
    @FormField(title = "Base Url", placeholder="https://007-JBO-707.mkforest.com", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String baseUrl;

    @FormField(title = "Client ID", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String clientId;

    @FormField(title = "Client Secret", schema = FormFieldSchema.STRING, type = FormFieldType.PASSWORD)
    private String clientSecret;
}
