package io.castled.apps.connectors.googlesheets;

import io.castled.OptionsReferences;
import io.castled.apps.syncconfigs.AppSyncConfig;
import io.castled.forms.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@GroupActivator(dependencies = {"object"}, group = MappingFormGroups.SYNC_MODE)
public class GoogleSheetsAppSyncConfig extends AppSyncConfig {

    @FormField(title = "Select the sheet to sync", type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.OBJECT, group = MappingFormGroups.OBJECT,
            optionsRef = @OptionsRef(value = OptionsReferences.OBJECT, type = OptionsRefType.DYNAMIC))
    private GoogleSheetsSyncObject object;
}
