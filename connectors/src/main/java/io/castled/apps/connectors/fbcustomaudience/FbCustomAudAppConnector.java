package io.castled.apps.connectors.fbcustomaudience;

import com.google.common.collect.Lists;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.connectors.fbcustomaudience.client.dtos.FbAudienceUserFields;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.dtos.FormFieldOption;

import java.util.List;

public class FbCustomAudAppConnector implements ExternalAppConnector<FbAppConfig, FbCustomAudDataSink, FbCustomAudAppSyncConfig> {
    @Override
    public List<FormFieldOption> getAllObjects(FbAppConfig config, FbCustomAudAppSyncConfig mappingConfig) {
        return null;
    }

    @Override
    public FbCustomAudDataSink getDataSink() {
        return ObjectRegistry.getInstance(FbCustomAudDataSink.class);
    }

    @Override
    public ExternalAppSchema getSchema(FbAppConfig config, FbCustomAudAppSyncConfig fbCaAppSyncConfig) {
        List<String> pkEligibles = Lists.newArrayList();
        pkEligibles.add(FbAudienceUserFields.EMAIL.getDisplayName());
        pkEligibles.add(FbAudienceUserFields.EXTERN_ID.getDisplayName());
        return new ExternalAppSchema(FbAdUtils.getSchema(), pkEligibles);
    }

    @Override
    public Class<FbCustomAudAppSyncConfig> getMappingConfigType() {
        return FbCustomAudAppSyncConfig.class;
    }

    @Override
    public List<AppSyncMode> getSyncModes(FbAppConfig config, FbCustomAudAppSyncConfig mappingConfig) {
        return Lists.newArrayList();
    }

    @Override
    public Class<FbAppConfig> getAppConfigType() {
        return FbAppConfig.class;
    }
}
