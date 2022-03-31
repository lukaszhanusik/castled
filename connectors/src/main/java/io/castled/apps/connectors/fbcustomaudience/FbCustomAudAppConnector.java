package io.castled.apps.connectors.fbcustomaudience;

import com.google.common.collect.Lists;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.connectors.fbcustomaudience.client.dtos.FbAudienceUserFields;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.mapping.FixedGroupAppField;
import io.castled.schema.mapping.MappingGroup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FbCustomAudAppConnector implements ExternalAppConnector<FbAppConfig, FbCustomAudDataWriter, FbCustomAudAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(FbAppConfig config, FbCustomAudAppSyncConfig mappingConfig) {
        return null;
    }

    @Override
    public FbCustomAudDataWriter getDataSink() {
        return ObjectRegistry.getInstance(FbCustomAudDataWriter.class);
    }

    @Override
    public ExternalAppSchema getSchema(FbAppConfig config, FbCustomAudAppSyncConfig fbCaAppSyncConfig) {
        return new ExternalAppSchema(FbAdSchemaUtils.getSchema());
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
    public List<MappingGroup> getMappingGroups(FbAppConfig config, FbCustomAudAppSyncConfig fbCustomAudAppSyncConfig) {
        // Fixed field group
        List<FixedGroupAppField> fixedGroupAppFields = Arrays.stream(FbAudienceUserFields.values())
                .map(userField -> new FixedGroupAppField(userField.getName(), userField.getDisplayName(), true))
                .collect(Collectors.toList());

        return MappingGroupAggregator.builder()
                .addFixedAppFields(fixedGroupAppFields)
                .build().getMappingGroups();
    }

    @Override
    public Class<FbAppConfig> getAppConfigType() {
        return FbAppConfig.class;
    }
}
