package io.castled.apps.connectors.activecampaign;

import io.castled.apps.BaseAppConfig;
import io.castled.forms.FormField;
import io.castled.forms.FormFieldSchema;
import io.castled.forms.FormFieldType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveCampaignAppConfig extends BaseAppConfig {

    @FormField(description = "All requests to the API are authenticated by providing your API key",
            title = "API KEY", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String apiKey;

    @FormField(description = "The API is accessed using the base URL that is specific to your account",
            title = "API URL", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String apiURL;
}
