package io.castled.apps.connectors.googlepubsub;

import io.castled.OptionsReferences;
import io.castled.apps.syncconfigs.AppSyncConfig;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@GroupActivator(dependencies = {"object"}, group = MappingFormGroups.SYNC_MODE)
public class GooglePubSubAppSyncConfig extends AppSyncConfig {

    @FormField(title = "Select Pub/Sub topic", type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.OBJECT, group = MappingFormGroups.OBJECT, optionsRef = @OptionsRef(value = OptionsReferences.OBJECT, type = OptionsRefType.DYNAMIC))
    private GooglePubSubTopicSyncObject object;

    @FormField(type = FormFieldType.RADIO_GROUP, schema = FormFieldSchema.ENUM, title = "Sync Mode", description = "Sync mode which controls whether records will be appended, updated or upserted", group = MappingFormGroups.SYNC_MODE,
            optionsRef = @OptionsRef(value = OptionsReferences.SYNC_MODE, type = OptionsRefType.DYNAMIC))
    private AppSyncMode mode;
}