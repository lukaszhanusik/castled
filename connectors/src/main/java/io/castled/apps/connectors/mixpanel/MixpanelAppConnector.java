package io.castled.apps.connectors.mixpanel;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.ExternalAppType;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.GenericSyncObject;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.apps.models.PrimaryKeyEligibles;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.models.AppFieldDetails;
import io.castled.schema.mapping.MappingGroup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class MixpanelAppConnector implements ExternalAppConnector<MixpanelAppConfig,
        MixpanelDataSink, MixpanelAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(MixpanelAppConfig mixpanelAppConfig, MixpanelAppSyncConfig mixpanelAppSyncConfig) {
        return Arrays.stream(MixpanelObject.values()).map(mixpanelObject -> new FormFieldOption(new GenericSyncObject(mixpanelObject.getName(),
                ExternalAppType.MIXPANEL), mixpanelObject.getName())).collect(Collectors.toList());
    }

    @Override
    public MixpanelDataSink getDataSink() {
        return ObjectRegistry.getInstance(MixpanelDataSink.class);
    }

    @Override
    public ExternalAppSchema getSchema(MixpanelAppConfig config, MixpanelAppSyncConfig mixpanelAppSyncConfig) {
        return new ExternalAppSchema(null, PrimaryKeyEligibles.autoDetect());
    }

    public List<AppSyncMode> getSyncModes(MixpanelAppConfig config, MixpanelAppSyncConfig mixpanelAppSyncConfig) {
        String object = mixpanelAppSyncConfig.getObject().getObjectName();
        if (MixpanelObject.EVENT.getName().equalsIgnoreCase(object)) {
            return Lists.newArrayList(AppSyncMode.INSERT);
        }
        if (MixpanelObject.USER_PROFILE.getName().equalsIgnoreCase(object) ||
                MixpanelObject.GROUP_PROFILE.getName().equalsIgnoreCase(object)) {
            return Lists.newArrayList(AppSyncMode.UPSERT);
        }
        return Lists.newArrayList(AppSyncMode.INSERT, AppSyncMode.UPSERT, AppSyncMode.UPDATE);
    }

    public Class<MixpanelAppSyncConfig> getMappingConfigType() {
        return MixpanelAppSyncConfig.class;
    }

    @Override
    public Class<MixpanelAppConfig> getAppConfigType() {
        return MixpanelAppConfig.class;
    }

    public List<MappingGroup> getMappingGroups(MixpanelAppConfig config, MixpanelAppSyncConfig mixpanelAppSyncConfig) {
        String object = mixpanelAppSyncConfig.getObject().getObjectName();
        if (MixpanelObject.EVENT.getName().equalsIgnoreCase(object)) {
            return getMappingGroupsForEventObject(config, mixpanelAppSyncConfig);
        }
        if (MixpanelObject.USER_PROFILE.getName().equalsIgnoreCase(object)) {
            return getMappingGroupsForUserProfileObject(config, mixpanelAppSyncConfig);
        }
        if (MixpanelObject.GROUP_PROFILE.getName().equalsIgnoreCase(object)) {
            return getMappingGroupsForGroupProfileObject(config, mixpanelAppSyncConfig);
        }
        return null;
    }

    private List<MappingGroup> getMappingGroupsForGroupProfileObject(MixpanelAppConfig config, MixpanelAppSyncConfig mixpanelAppSyncConfig) {

        List<AppFieldDetails> primaryKeys = Lists.newArrayList();
        primaryKeys.add(AppFieldDetails.builder()
                .internalName(MixpanelObjectFields.GROUP_PROFILE_FIELDS.GROUP_ID.getFieldName())
                .displayName(MixpanelObjectFields.GROUP_PROFILE_FIELDS.GROUP_ID.getFieldTitle())
                .build());

        MappingGroupAggregator.Builder builder = MappingGroupAggregator.builder();
        return builder.addPrimaryKeys(primaryKeys).addMiscellaneousFields(false).build().getMappingGroups();
    }

    private List<MappingGroup> getMappingGroupsForUserProfileObject(MixpanelAppConfig config, MixpanelAppSyncConfig mixpanelAppSyncConfig) {

        List<AppFieldDetails> primaryKeys = Lists.newArrayList();
        primaryKeys.add(AppFieldDetails.builder()
                .internalName(MixpanelObjectFields.USER_PROFILE_FIELDS.DISTINCT_ID.getFieldName())
                .displayName(MixpanelObjectFields.USER_PROFILE_FIELDS.DISTINCT_ID.getFieldTitle())
                .build());

        List<AppFieldDetails> destinationFields = Lists.newArrayList();
        destinationFields.add(AppFieldDetails.builder()
                .internalName(MixpanelObjectFields.USER_PROFILE_FIELDS.FIRST_NAME.getFieldName())
                .displayName(MixpanelObjectFields.USER_PROFILE_FIELDS.FIRST_NAME.getFieldTitle())
                .optional(true)
                .build());
        destinationFields.add(AppFieldDetails.builder()
                .internalName(MixpanelObjectFields.USER_PROFILE_FIELDS.LAST_NAME.getFieldName())
                .displayName(MixpanelObjectFields.USER_PROFILE_FIELDS.LAST_NAME.getFieldTitle())
                .optional(true)
                .build());
        destinationFields.add(AppFieldDetails.builder()
                .internalName(MixpanelObjectFields.USER_PROFILE_FIELDS.EMAIL.getFieldName())
                .displayName(MixpanelObjectFields.USER_PROFILE_FIELDS.EMAIL.getFieldTitle())
                .optional(false)
                .build());

        MappingGroupAggregator.Builder builder = MappingGroupAggregator.builder();
        return builder.addPrimaryKeys(primaryKeys).addDestinationFields(destinationFields).addMiscellaneousFields(false).build().getMappingGroups();
    }

    private List<MappingGroup> getMappingGroupsForEventObject(MixpanelAppConfig config, MixpanelAppSyncConfig mixpanelAppSyncConfig) {

        List<AppFieldDetails> importantParameters = Lists.newArrayList();
        importantParameters.add(AppFieldDetails.builder()
                .title("Column identifying the user associated with the event")
                .description("This field will be used to uniquely identifying the user associated with the event")
                .internalName(MixpanelObjectFields.EVENT_FIELDS.DISTINCT_ID.getFieldName())
                .displayName(MixpanelObjectFields.EVENT_FIELDS.DISTINCT_ID.getFieldTitle())
                .optional(false)
                .build());

        importantParameters.add(AppFieldDetails.builder()
                .title("Column identifying Event Timestamp")
                .description("Column identifying Event Timestamp")
                .internalName(MixpanelObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldName())
                .displayName(MixpanelObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldTitle())
                .optional(true)
                .build());

        MappingGroupAggregator.Builder builder = MappingGroupAggregator.builder();
        return builder.addImportantParameters(importantParameters).addMiscellaneousFields(false).build().getMappingGroups();
    }
}
