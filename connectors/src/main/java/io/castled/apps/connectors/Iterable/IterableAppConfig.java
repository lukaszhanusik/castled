package io.castled.apps.connectors.Iterable;

import io.castled.apps.BaseAppConfig;
import io.castled.forms.FormField;
import io.castled.forms.FormFieldSchema;
import io.castled.forms.FormFieldType;
import lombok.Getter;

@Getter
public class IterableAppConfig extends BaseAppConfig {

    @FormField(description = "API key to read and write Iterable data from a server-side application", title = "API Key", schema = FormFieldSchema.STRING, type = FormFieldType.PASSWORD)
    private String apiKey;
}
