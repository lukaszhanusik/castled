package io.castled.warehouses.connectors.postgres;

import io.castled.forms.FormField;
import io.castled.forms.FormFieldSchema;
import io.castled.forms.FormFieldType;
import io.castled.warehouses.TunneledWarehouseConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostgresWarehouseConfig extends TunneledWarehouseConfig {

    @FormField(title = "Database Server Host", placeholder = "e.g. example.mydomain.com", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String serverHost;

    @FormField(title = "Database Server Port", placeholder = "e.g. 5432", schema = FormFieldSchema.NUMBER, type = FormFieldType.TEXT_BOX)
    private int serverPort;

    @FormField(title = "Database Name", placeholder = "e.g. demo_db", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String dbName;

    @FormField(title = "Database User", placeholder = "e.g. db_user", schema = FormFieldSchema.STRING, type = FormFieldType.TEXT_BOX)
    private String dbUser;

    @FormField(title = "Database Password", schema = FormFieldSchema.STRING, type = FormFieldType.PASSWORD)
    private String dbPassword;
}
