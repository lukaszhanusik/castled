package io.castled.apps.connectors.fbconversion;

import io.castled.apps.BaseAppConfig;
import io.castled.forms.FormField;
import io.castled.forms.FormFieldSchema;
import io.castled.forms.FormFieldType;
import lombok.Getter;

@Getter
public class FbConversionAppConfig extends BaseAppConfig {

    @FormField(description = "Pixel Id for the Conversion Api", title = "Pixel ID", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String pixelId;

    @FormField(description = "Access Token for the Conversion Api", title = "Access Token", schema = FormFieldSchema.STRING, type = FormFieldType.PASSWORD)
    private String accessToken;
}
