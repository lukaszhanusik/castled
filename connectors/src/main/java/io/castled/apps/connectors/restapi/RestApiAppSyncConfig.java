package io.castled.apps.connectors.restapi;

import io.castled.OptionsReferences;
import io.castled.apps.syncconfigs.AppSyncConfig;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestApiAppSyncConfig extends AppSyncConfig {

    @FormField(required = false, type = FormFieldType.TEXT_BOX, title = "Property Name in the input object", description = "Property name in the input object")
    private String propertyName;

    @FormField(required = false, type = FormFieldType.TEXT_BOX, schema = FormFieldSchema.NUMBER, title = "Batch Size", description = "Batch Size")
    private Integer batchSize;

    @FormField(required = false, type = FormFieldType.TEXT_BOX, schema = FormFieldSchema.NUMBER, title = "Parallel Invocation Count", description = "Parallel Invocation Count")
    private Integer parallelThreads;

    @FormField(type = FormFieldType.RADIO_GROUP, schema = FormFieldSchema.ENUM, title = "Sync Mode", description = "Sync mode which controls whether records will be appended, updated or upserted", group = MappingFormGroups.SYNC_MODE,
            optionsRef = @OptionsRef(value = OptionsReferences.SYNC_MODE, type = OptionsRefType.DYNAMIC))
    private AppSyncMode mode;
}
