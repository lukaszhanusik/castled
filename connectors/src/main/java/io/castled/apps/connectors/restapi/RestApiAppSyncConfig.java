package io.castled.apps.connectors.restapi;

import io.castled.apps.syncconfigs.BaseAppSyncConfig;
import io.castled.forms.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@GroupActivator(dependencies = {"bulk"}, group = "bulk")
public class RestApiAppSyncConfig extends BaseAppSyncConfig {

    @FormField(type = FormFieldType.TEXT_BOX, title = "Parallelism", description = "Parallelism")
    private Integer parallelism;

    @FormField(required = false, description = "Bulk update", title = "Enable bulk", schema = FormFieldSchema.BOOLEAN, type = FormFieldType.CHECK_BOX)
    private boolean bulk;

    @FormField(type = FormFieldType.TEXT_BOX, title = "Json Array path", placeholder = "parent.child.subchild", description = "Json Array Path", group = "bulk")
    private String jsonPath;

    @FormField(type = FormFieldType.TEXT_BOX, title = "Batch Size", description = "Batch Size", group = "bulk")
    private Integer batchSize;
}
