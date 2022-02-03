package io.castled.warehouses.connectors.bigquery;

import io.castled.OptionsReferences;
import io.castled.commons.models.ServiceAccountDetails;
import io.castled.forms.*;
import io.castled.warehouses.WarehouseConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@CodeBlock(title = "Run the following commands on the google cloud console",
        dependencies = {"projectId", "bucketName", "serviceAccount"}
        , snippets = {@CodeSnippet(title = "BQ Data Viewer Role", ref = "bq_data_viewer_access"),
        @CodeSnippet(title = "BQ User Role", ref = "bq_data_user_access"),
        @CodeSnippet(title = "GCP Storage Admin Role", ref = "gcp_storage_admin_access")})
@GroupActivator(dependencies = {"projectId", "bucketName", "location"}, group = "service_account")
public class BigQueryWarehouseConfig extends WarehouseConfig {

    @FormField(description = "Project Id", title = "Project Id", placeholder = "e.g. demo-project-12345", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String projectId;

    @FormField(description = "GCS Bucket Name", title = "GCS Bucket Name", placeholder = "e.g sample-bq-bucket", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String bucketName;

    @FormField(description = "Dataset location", title = "Dataset location", placeholder = "e.g US-WEST4", schema = FormFieldSchema.STRING, type = FormFieldType.DROP_DOWN,
            optionsRef = @OptionsRef(value = OptionsReferences.BQ_LOCATIONS, type = OptionsRefType.STATIC))
    private String location;

    @FormField(type = FormFieldType.JSON_FILE, schema = FormFieldSchema.OBJECT, description = "Service Account Json File", title = "Service Account Json File", group = "service_account")
    private ServiceAccountDetails serviceAccount;
}
