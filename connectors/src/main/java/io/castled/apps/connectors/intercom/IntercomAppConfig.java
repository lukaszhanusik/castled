package io.castled.apps.connectors.intercom;

import io.castled.apps.BaseAppConfig;
import io.castled.forms.FormField;
import io.castled.forms.FormFieldSchema;
import io.castled.forms.FormFieldType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntercomAppConfig extends BaseAppConfig {

    @FormField(description = "Intercom Access Token", title = "Access Token", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String accessToken;
}
