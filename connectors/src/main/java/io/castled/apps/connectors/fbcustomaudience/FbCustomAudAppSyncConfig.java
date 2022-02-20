package io.castled.apps.connectors.fbcustomaudience;

import io.castled.OptionsReferences;
import io.castled.apps.syncconfigs.BaseAppSyncConfig;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.*;
import lombok.Getter;
import lombok.Setter;

@GroupActivator(dependencies = {"accountId"}, group = MappingFormGroups.OBJECT)
@Getter
@Setter
public class FbCustomAudAppSyncConfig extends BaseAppSyncConfig {

    @FormField(type = FormFieldType.DROP_DOWN, title = "Ads Account", description = "Facebook ads account",
            optionsRef = @OptionsRef(value = OptionsReferences.FB_ADS_ACCOUNTS, type = OptionsRefType.DYNAMIC))
    private String accountId;

    @FormField(type = FormFieldType.TEXT_BOX, title = "Custom Audience Name", description = "Customer list custom audience name", schema = FormFieldSchema.STRING, group = MappingFormGroups.OBJECT)
    private String customAudienceName;

    @FormField(type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.ENUM, title = "Sync Mode", description = "Sync mode which controls whether records will be appended, updated or upserted", group = MappingFormGroups.SYNC_MODE,
            optionsRef = @OptionsRef(value = OptionsReferences.SYNC_MODE, type = OptionsRefType.DYNAMIC))
    private AppSyncMode mode;

    @FormField(type = FormFieldType.RADIO_BOX, schema = FormFieldSchema.BOOLEAN, title = "Hashing Required?", description = "Decides whether Castled should hash Personally identifiable Information(PII).", group = MappingFormGroups.SYNC_MODE,
            optionsRef = @OptionsRef(value = OptionsReferences.FB_AUDIENCE_HASHING, type = OptionsRefType.STATIC), required = true)
    private boolean hashingRequired;
}
