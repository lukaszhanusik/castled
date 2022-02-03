package io.castled.warehouses.connectors.redshift;

import com.amazonaws.regions.Regions;
import io.castled.forms.FormField;
import io.castled.forms.FormFieldSchema;
import io.castled.forms.FormFieldType;
import io.castled.warehouses.TunneledWarehouseConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedshiftWarehouseConfig extends TunneledWarehouseConfig {

    @FormField(description = "Database Server Host", title = "Database Server Host", placeholder = "e.g. examplecluster.abc123xyz789.us-west-2.rds.amazonaws.com", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String serverHost;

    @FormField(description = "Database Server Port", title = "Database Server Port", placeholder = "e.g. 5439", schema = FormFieldSchema.NUMBER, type = FormFieldType.TEXT_BOX)
    private int serverPort;

    @FormField(description = "Database Name", title = "Database Name", placeholder = "e.g. demo_db", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String dbName;

    @FormField(description = "Database User", title = "Database User", placeholder = "e.g. db_user", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String dbUser;

    @FormField(description = "Database Password", title = "Database Password", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String dbPassword;

    @FormField(description = "S3 Bucket to be used as the staging area", title = "S3 Bucket", placeholder = "e.g. s3://anycompany-stage-saeast1-12345-dev/sap/br/customers/validated/dt=2021-03-01/table_customers_20210301.snappy.parquet", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String s3Bucket;

    @FormField(description = "S3 Access Key Id", title = "S3 Access Key Id", placeholder = "e.g. AKIAIOSFODNN7EXAMPLE", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String accessKeyId;

    @FormField(description = "S3 Access Key Secret", title = "S3 Access Key Secret", placeholder = "e.g. wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String accessKeySecret;

    @FormField(description = "S3 Bucket Location", title = "S3 Bucket Location", placeholder = "e.g. US_EAST_1", schema = FormFieldSchema.ENUM, type = FormFieldType.TEXT_BOX)
    private Regions region;

}
