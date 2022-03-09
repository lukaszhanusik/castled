package io.castled.apps.connectors.mixpanel;

import io.castled.OptionsReferences;
import io.castled.apps.models.GenericSyncObject;
import io.castled.apps.syncconfigs.BaseAppSyncConfig;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.*;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@GroupActivator(dependencies = {"object"}, group = MappingFormGroups.SYNC_MODE)
@GroupActivator(dependencies = {"object"}, condition = "object.objectName == 'Event'", group = "eventTypeGroup")
@GroupActivator(dependencies = {"object"}, condition = "object.objectName == 'User Profile'", group = "userProfileGroup")
@GroupActivator(dependencies = {"object"}, condition = "object.objectName == 'Group Profile'", group = "groupProfileGroup")
@Getter
@Setter
public class MixpanelAppSyncConfig extends BaseAppSyncConfig {

    @FormField(title = "Select Object to sync", type = FormFieldType.RADIO_BOX, schema = FormFieldSchema.OBJECT, group = MappingFormGroups.OBJECT,
            optionsRef = @OptionsRef(value = OptionsReferences.OBJECT, type = OptionsRefType.DYNAMIC))
    private GenericSyncObject object;

    @FormField(type = FormFieldType.TEXT_BOX, group = "eventTypeGroup", title = "Event Name", description = "Name of the event")
    private String eventName;

    @NotNull
    @FormField(type = FormFieldType.TEXT_BOX, group = "groupProfileGroup", title = "Mixpanel Group Key", description = "Column which identifies the event ID")
    private String groupKey;

    @FormField(type = FormFieldType.RADIO_BOX, schema = FormFieldSchema.ENUM, title = "Sync Mode", description = "Sync mode which controls whether records will be appended, updated or upserted", group = MappingFormGroups.SYNC_MODE,
            optionsRef = @OptionsRef(value = OptionsReferences.SYNC_MODE, type = OptionsRefType.DYNAMIC))
    private AppSyncMode mode;
}
