package io.castled.apps.connectors.intercom;

import io.castled.OptionsReferences;
import io.castled.apps.models.GenericSyncObject;
import io.castled.apps.syncconfigs.BaseAppSyncConfig;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@GroupActivator(dependencies = {"object"}, group = MappingFormGroups.SYNC_MODE)
@GroupActivator(dependencies = {"object"}, condition = "object.objectName == 'Lead' || object.objectName == 'User'", group = "association")
public class IntercomAppSyncConfig extends BaseAppSyncConfig {

    @FormField(title = "Select object to configure", type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.OBJECT, group = MappingFormGroups.OBJECT, optionsRef = @OptionsRef(value = OptionsReferences.OBJECT, type = OptionsRefType.DYNAMIC))
    private GenericSyncObject object;

    @FormField(type = FormFieldType.RADIO_GROUP, schema = FormFieldSchema.ENUM, title = "Sync Mode", description = "Sync mode which controls whether records will be appended, updated or upserted", group = MappingFormGroups.SYNC_MODE,
            optionsRef = @OptionsRef(value = OptionsReferences.SYNC_MODE, type = OptionsRefType.DYNAMIC))
    private AppSyncMode mode;

    @FormField(required = false, title = "Associate company to contacts. You will be asked to map company id on the mapping screen", schema = FormFieldSchema.BOOLEAN, type = FormFieldType.CHECK_BOX, group = "association")
    private boolean associateCompany;
}