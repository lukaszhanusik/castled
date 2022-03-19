package io.castled.apps.connectors.restapi;

import io.castled.apps.syncconfigs.BaseAppSyncConfig;
import io.castled.forms.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@GroupActivator(dependencies = {"bulk"}, group = "bulk")
public class RestApiAppSyncConfig extends BaseAppSyncConfig {

    @FormField(type = FormFieldType.TEXT_BOX, title = "Parallelism", description = "Castled with hit the destination apis with the parallelism defined here")
    private Integer parallelism;

    @FormField(required = false, description = "Enable if you have a bulk api", title = "Enable bulk", schema = FormFieldSchema.BOOLEAN, type = FormFieldType.CHECK_BOX)
    private boolean bulk;

    @FormField(type = FormFieldType.TEXT_BOX, title = "Json Array Path", placeholder = "parent.child.subchild", description = "Path in the json which contains the json array of mustache template records", group = "bulk")
    private String jsonPath;

    @FormField(type = FormFieldType.TEXT_BOX, title = "Batch Size", description = "Castled will batch the records based on this configuration", group = "bulk")
    private Integer batchSize;
}
