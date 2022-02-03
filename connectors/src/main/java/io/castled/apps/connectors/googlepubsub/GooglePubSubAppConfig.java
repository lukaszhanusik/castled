package io.castled.apps.connectors.googlepubsub;

import io.castled.apps.AppConfig;
import io.castled.commons.models.ServiceAccountDetails;
import io.castled.forms.FormField;
import io.castled.forms.FormFieldSchema;
import io.castled.forms.FormFieldType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GooglePubSubAppConfig extends AppConfig {

    @FormField(title = "Project ID", placeholder = "", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String projectID;

    @FormField(description = "Service Account", title = "Service Account Json file", type = FormFieldType.JSON_FILE, schema = FormFieldSchema.OBJECT)
    private ServiceAccountDetails serviceAccountDetails;
}
