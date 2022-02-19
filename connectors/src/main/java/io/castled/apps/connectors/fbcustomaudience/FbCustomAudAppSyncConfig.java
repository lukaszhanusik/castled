package io.castled.apps.connectors.fbcustomaudience;

import io.castled.OptionsReferences;
import io.castled.apps.connectors.googleads.GoogleAdsFormGroups;
import io.castled.apps.syncconfigs.AppSyncConfig;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.*;
import lombok.Getter;

@GroupActivator(dependencies = {"accountId"}, group = GoogleAdsFormGroups.LOGIN_ACCOUNT_ID)
@Getter
public class FbCustomAudAppSyncConfig extends AppSyncConfig {

    @FormField(type = FormFieldType.DROP_DOWN, title = "Ads Account", description = "Facebook ads account",
            optionsRef = @OptionsRef(value = OptionsReferences.FB_ADS_ACCOUNTS, type = OptionsRefType.DYNAMIC))
    private String accountId;

    @FormField(type = FormFieldType.TEXT_BOX, title = "Custom Audience Name", description = "Customer list custom audience name", schema = FormFieldSchema.STRING)
    private String customAudienceName;

    @FormField(type = FormFieldType.RADIO_GROUP, schema = FormFieldSchema.ENUM, title = "Sync Mode", description = "Sync mode which controls whether records will be appended, updated or upserted", group = MappingFormGroups.SYNC_MODE,
            optionsRef = @OptionsRef(value = OptionsReferences.SYNC_MODE, type = OptionsRefType.DYNAMIC))
    private AppSyncMode mode;
}
