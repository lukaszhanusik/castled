package io.castled.warehouses.connectors.redshift;

import com.amazonaws.regions.Regions;
import io.castled.OptionsReferences;
import io.castled.forms.*;
import io.castled.warehouses.TunneledWarehouseConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedshiftWarehouseConfig extends TunneledWarehouseConfig {

    @FormField(title = "Database Server Host", placeholder = "e.g. examplecluster.abc123xyz789.us-west-2.rds.amazonaws.com", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String serverHost;

    @FormField(title = "Database Server Port", placeholder = "e.g. 5439", schema = FormFieldSchema.NUMBER, type = FormFieldType.TEXT_BOX)
    private int serverPort;

    @FormField(title = "Database Name", placeholder = "e.g. demo_db", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String dbName;

    @FormField(title = "Database User", placeholder = "e.g. db_user", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String dbUser;

    @FormField(title = "Database Password", schema = FormFieldSchema.STRING, type = FormFieldType.PASSWORD)
    private String dbPassword;

    @FormField(title = "S3 Bucket to be used as staging area", placeholder = "e.g. castled-bucket", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String s3Bucket;

    @FormField(title = "S3 Access Key Id", placeholder = "e.g. AKIAIOSFODNN7EXAMPLE", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String accessKeyId;

    @FormField(title = "S3 Access Key Secret", placeholder = "e.g. wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String accessKeySecret;

    @FormField(title = "S3 Bucket Location", schema = FormFieldSchema.ENUM,
            type = FormFieldType.DROP_DOWN, optionsRef = @OptionsRef(value = OptionsReferences.AWS_REGIONS, type = OptionsRefType.STATIC))
    private Regions region;

}
