package io.castled.apps.connectors.customerio;

import io.castled.OptionsReferences;
import io.castled.apps.models.GenericSyncObject;
import io.castled.apps.syncconfigs.BaseAppSyncConfig;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.*;
import lombok.Getter;
import lombok.Setter;

@GroupActivator(dependencies = {"object"}, group = MappingFormGroups.SYNC_MODE)
@GroupActivator(dependencies = {"object"}, condition = "object.objectName == 'Event'", group = "eventTypeGroup")
@GroupActivator(dependencies = {"object"}, condition = "object.objectName == 'Person'", group = "personGroup")
@Getter
@Setter
public class CustomerIOAppSyncConfig extends BaseAppSyncConfig {

    @FormField(title = "Select Object to sync", description = "Object you want to sync to the destination",
            type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.OBJECT, group = MappingFormGroups.OBJECT,
            optionsRef = @OptionsRef(value = OptionsReferences.OBJECT, type = OptionsRefType.DYNAMIC))
    private GenericSyncObject object;

    @FormField(title = "Event Type for tracking", description = "Select the event type you want to track",
            type = FormFieldType.DROP_DOWN, group = "eventTypeGroup",
            optionsRef = @OptionsRef(value = OptionsReferences.CIO_EVENT_TYPES, type = OptionsRefType.DYNAMIC))
    private String eventType;

    @FormField(title = "Sync Mode", description = "How you want to sync the data to the destination",
            type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.ENUM, group = MappingFormGroups.SYNC_MODE,
            optionsRef = @OptionsRef(value = OptionsReferences.SYNC_MODE, type = OptionsRefType.DYNAMIC))
    private AppSyncMode mode;
}
