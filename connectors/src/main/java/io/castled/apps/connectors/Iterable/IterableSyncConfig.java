package io.castled.apps.connectors.Iterable;

import io.castled.OptionsReferences;
import io.castled.apps.connectors.Iterable.client.IterableFormGroups;
import io.castled.apps.models.GenericSyncObject;
import io.castled.apps.syncconfigs.BaseAppSyncConfig;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@GroupActivator(dependencies = {"object"}, group = IterableFormGroups.SYNC_MODE)
@GroupActivator(dependencies = {"object"}, condition = "object.objectName == 'events'", group = IterableFormGroups.TEMPLATE_ID)
@GroupActivator(dependencies = {"object"}, condition = "object.objectName == 'events'", group = IterableFormGroups.CAMPAIGN_ID)
@GroupActivator(dependencies = {"object"}, condition = "object.objectName == 'catalogs'", group = IterableFormGroups.CATALOG)
public class IterableSyncConfig extends BaseAppSyncConfig {

    @FormField(title = "Select Iterable object to sync", type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.OBJECT, group = IterableFormGroups.OBJECT,
            optionsRef = @OptionsRef(value = OptionsReferences.OBJECT, type = OptionsRefType.DYNAMIC))
    private GenericSyncObject object;

    @FormField(title = "Select Campaign Id", description = "If Iterable campaignId field is provided a mapping in the next page, it will override this config",
            type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.NUMBER, group = IterableFormGroups.CAMPAIGN_ID,
            optionsRef = @OptionsRef(value = OptionsReferences.ITERABLE_CAMPAIGN_IDS, type = OptionsRefType.DYNAMIC), required = false)
    private String campaignId;

    @FormField(title = "Select Template Id", description = "If Iterable templateId field is provided a mapping in the next page, it will override this config",
            type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.NUMBER, group = IterableFormGroups.TEMPLATE_ID,
            optionsRef = @OptionsRef(value = OptionsReferences.ITERABLE_TEMPLATE_IDS, type = OptionsRefType.DYNAMIC), required = false)
    private String templateId;

    @FormField(title = "Select Catalog to which items are synced", description = "All the items will be synced to the selected Catalog",
            type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.STRING, group = IterableFormGroups.CATALOG,
            optionsRef = @OptionsRef(value = OptionsReferences.ITERABLE_CATALOGS, type = OptionsRefType.DYNAMIC), required = true)
    private String catalogName;

    @FormField(type = FormFieldType.RADIO_GROUP, schema = FormFieldSchema.ENUM, title = "Sync Mode",
            description = "Sync mode controls whether records will be appended, updated or upserted", group = IterableFormGroups.SYNC_MODE,
            optionsRef = @OptionsRef(value = OptionsReferences.SYNC_MODE, type = OptionsRefType.DYNAMIC))
    private AppSyncMode mode;
}
