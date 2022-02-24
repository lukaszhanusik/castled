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
import io.castled.schema.models.SchemaType;
import io.castled.utils.MappingGroupUtil;

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
                .type(SchemaType.STRING.getDisplayName())
                .optional(false)
                .build());

        List<MappingGroup> mappingGroups = Lists.newArrayList();
        mappingGroups.add(MappingGroupUtil.constructPrimaryKeyGroup(primaryKeys));
        mappingGroups.add(MappingGroupUtil.constructMiscellaneousFieldGroup(false));

        MappingGroupAggregator.Builder builder = MappingGroupAggregator.builder();
        return builder.addPrimaryKeys(primaryKeys).addMiscellaneousFields(false).build().getMappingGroups();
        // return mappingGroups;
    }

    private List<MappingGroup> getMappingGroupsForUserProfileObject(MixpanelAppConfig config, MixpanelAppSyncConfig mixpanelAppSyncConfig) {

        List<AppFieldDetails> primaryKeys = Lists.newArrayList();
        primaryKeys.add(AppFieldDetails.builder()
                .internalName(MixpanelObjectFields.USER_PROFILE_FIELDS.DISTINCT_ID.getFieldName())
                .displayName(MixpanelObjectFields.USER_PROFILE_FIELDS.DISTINCT_ID.getFieldTitle())
                .type(SchemaType.STRING.getDisplayName())
                .optional(false)
                .build());

        List<AppFieldDetails> destinationFields = Lists.newArrayList();
        destinationFields.add(AppFieldDetails.builder()
                .internalName(MixpanelObjectFields.USER_PROFILE_FIELDS.FIRST_NAME.getFieldName())
                .displayName(MixpanelObjectFields.USER_PROFILE_FIELDS.FIRST_NAME.getFieldTitle())
                .type(SchemaType.STRING.getDisplayName())
                .optional(true)
                .build());
        destinationFields.add(AppFieldDetails.builder()
                .internalName(MixpanelObjectFields.USER_PROFILE_FIELDS.LAST_NAME.getFieldName())
                .displayName(MixpanelObjectFields.USER_PROFILE_FIELDS.LAST_NAME.getFieldTitle())
                .type(SchemaType.STRING.getDisplayName())
                .optional(true)
                .build());
        destinationFields.add(AppFieldDetails.builder()
                .internalName(MixpanelObjectFields.USER_PROFILE_FIELDS.EMAIL.getFieldName())
                .displayName(MixpanelObjectFields.USER_PROFILE_FIELDS.EMAIL.getFieldTitle())
                .type(SchemaType.STRING.getDisplayName())
                .optional(false)
                .build());

        List<MappingGroup> mappingGroups = Lists.newArrayList();
        mappingGroups.add(MappingGroupUtil.constructPrimaryKeyGroup(primaryKeys));
        mappingGroups.add(MappingGroupUtil.constructDestinationFieldGroup(destinationFields));
        mappingGroups.add(MappingGroupUtil.constructMiscellaneousFieldGroup(false));

        MappingGroupAggregator.Builder builder = MappingGroupAggregator.builder();
        return builder.addPrimaryKeys(primaryKeys).addDestinationFields(destinationFields).addMiscellaneousFields(false).build().getMappingGroups();
        //return mappingGroups;
    }

    private List<MappingGroup> getMappingGroupsForEventObject(MixpanelAppConfig config, MixpanelAppSyncConfig mixpanelAppSyncConfig) {

        List<AppFieldDetails> importantParameters = Lists.newArrayList();
        importantParameters.add(AppFieldDetails.builder()
                .title("Column identifying the user associated with the event")
                .description("This field will be used to uniquely identifying the user associated with the event")
                .internalName(MixpanelObjectFields.EVENT_FIELDS.DISTINCT_ID.getFieldName())
                .displayName(MixpanelObjectFields.EVENT_FIELDS.DISTINCT_ID.getFieldTitle())
                .type(SchemaType.STRING.getDisplayName())
                .optional(false)
                .build());

        importantParameters.add(AppFieldDetails.builder()
                .title("Column identifying Event Timestamp")
                .description("Column identifying Event Timestamp")
                .internalName(MixpanelObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldName())
                .displayName(MixpanelObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldTitle())
                .type(SchemaType.TIMESTAMP.getDisplayName())
                .optional(true)
                .build());

        List<MappingGroup> mappingGroups = Lists.newArrayList();
        mappingGroups.add(MappingGroupUtil.constructImportantParameterGroup(importantParameters));
        mappingGroups.add(MappingGroupUtil.constructMiscellaneousFieldGroup(false));

        MappingGroupAggregator.Builder builder = MappingGroupAggregator.builder();
        return builder.addImportantParameters(importantParameters).addMiscellaneousFields(false).build().getMappingGroups();
        //return mappingGroups;
    }

      /*
    public PipelineConfigDTO validateAndEnrichPipelineConfig(PipelineConfigDTO pipelineConfig) throws BadRequestException {
        MixpanelAppSyncConfig mixpanelAppSyncConfig = (MixpanelAppSyncConfig) pipelineConfig.getAppSyncConfig();

        pipelineConfig.getMapping().getFieldMappings();
        String objectName = ((MixpanelAppSyncConfig)pipelineConfig.getAppSyncConfig()).getObject().getObjectName();

        if(MixpanelObject.EVENT.getName().equalsIgnoreCase(objectName)) {
            List<MappingGroup> mappingGroups = getMappingGroupsForEventObject(null,mixpanelAppSyncConfig);
            enrichPipelineConfigForEventObject(pipelineConfig, mixpanelAppSyncConfig,mappingGroups);
        }
        if(MixpanelObject.USER_PROFILE.getName().equalsIgnoreCase(objectName)){
            enrichPipelineConfigForUserProfileObject(pipelineConfig, mixpanelAppSyncConfig);
        }
        if(MixpanelObject.GROUP_PROFILE.getName().equalsIgnoreCase(objectName)){
            enrichPipelineConfigForGroupProfileObject(pipelineConfig, mixpanelAppSyncConfig);
        }
        return pipelineConfig;
    }

   private void enrichPipelineConfigForUserProfileObject(PipelineConfigDTO pipelineConfig, MixpanelAppSyncConfig mixpanelAppSyncConfig) throws BadRequestException {
        String distinctID = Optional.ofNullable(mixpanelAppSyncConfig.getDistinctID()).orElseThrow(()->new BadRequestException("Column uniquely identifying the User is mandatory"));

    }

    private void enrichPipelineConfigForGroupProfileObject(PipelineConfigDTO pipelineConfig, MixpanelAppSyncConfig mixpanelAppSyncConfig) throws BadRequestException{
        String groupID = Optional.ofNullable(mixpanelAppSyncConfig.getGroupID()).orElseThrow(()->new BadRequestException("Column uniquely identifying the Group is mandatory"));
        String groupKey = Optional.ofNullable(mixpanelAppSyncConfig.getGroupKey()).orElseThrow(()->new BadRequestException("Group key is mandatory"));

    }

    private void enrichPipelineConfigForEventObject(PipelineConfigDTO pipelineConfig, MixpanelAppSyncConfig mixpanelAppSyncConfig, List<MappingGroup> mappingGroups) throws BadRequestException{

        String eventId = Optional.ofNullable(mixpanelAppSyncConfig.getEventID()).orElseThrow(()->new BadRequestException("Column uniquely identifying the Event is mandatory"));
        Optional.ofNullable(mixpanelAppSyncConfig.getEventName()).orElseThrow(()-> new BadRequestException("Event Name is mandatory"));

    }


    @Override
    public RecordSchema enrichWarehouseASchema(AppSyncConfigDTO appSyncConfigDTO , RecordSchema warehouseSchema) {

        MixpanelAppSyncConfig mixpanelAppSyncConfig = ((MixpanelAppSyncConfig)appSyncConfigDTO.getAppSyncConfig());
        String objectName = mixpanelAppSyncConfig.getObject().getObjectName();

        if(MixpanelObject.EVENT.getName().equalsIgnoreCase(objectName)) {
            List<String> warehouseFieldsToBeRemoved = getAllReservedFieldsForEventProfile(mixpanelAppSyncConfig);
            List<FieldSchema> fieldSchemas = warehouseSchema.getFieldSchemas().stream().filter(schema -> warehouseFieldsToBeRemoved.contains(schema.getName())).collect(Collectors.toList());
            warehouseSchema.removeFieldSchema(fieldSchemas);
        }
        if(MixpanelObject.USER_PROFILE.getName().equalsIgnoreCase(objectName)){
            List<String> warehouseFieldsToBeRemoved = getAllReservedFieldsForUserProfile(mixpanelAppSyncConfig);
            List<FieldSchema> fieldSchemas = warehouseSchema.getFieldSchemas().stream().filter(schema -> warehouseFieldsToBeRemoved.contains(schema.getName())).collect(Collectors.toList());
            warehouseSchema.removeFieldSchema(fieldSchemas);
        }
        if(MixpanelObject.GROUP_PROFILE.getName().equalsIgnoreCase(objectName)){
            List<String> warehouseFieldsToBeRemoved = getAllReservedFieldsForGroupProfile(mixpanelAppSyncConfig);
            List<FieldSchema> fieldSchemas = warehouseSchema.getFieldSchemas().stream().filter(schema -> warehouseFieldsToBeRemoved.contains(schema.getName())).collect(Collectors.toList());
            warehouseSchema.removeFieldSchema(fieldSchemas);
        }

        return warehouseSchema;
    }

    private List<String> getAllReservedFieldsForUserProfile(MixpanelAppSyncConfig mixpanelAppSyncConfig ){
        List<String> reservedFields = Lists.newArrayList();
        CollectionUtils.addIgnoreNull(reservedFields,mixpanelAppSyncConfig.getDistinctID());
        CollectionUtils.addIgnoreNull(reservedFields,mixpanelAppSyncConfig.getFirstName());
        CollectionUtils.addIgnoreNull(reservedFields,mixpanelAppSyncConfig.getLastName());
        CollectionUtils.addIgnoreNull(reservedFields,mixpanelAppSyncConfig.getUserEmail());
        return reservedFields;
    }

    private List<String> getAllReservedFieldsForGroupProfile(MixpanelAppSyncConfig mixpanelAppSyncConfig ){
        List<String> reservedFields = Lists.newArrayList();
        CollectionUtils.addIgnoreNull(reservedFields,mixpanelAppSyncConfig.getGroupID());
        CollectionUtils.addIgnoreNull(reservedFields,mixpanelAppSyncConfig.getGroupKey());
        return reservedFields;
    }

    private List<String> getAllReservedFieldsForEventProfile(MixpanelAppSyncConfig mixpanelAppSyncConfig ){
        List<String> reservedFields = Lists.newArrayList();
        CollectionUtils.addIgnoreNull(reservedFields,mixpanelAppSyncConfig.getEventID());
        CollectionUtils.addIgnoreNull(reservedFields,mixpanelAppSyncConfig.getEventIP());
        CollectionUtils.addIgnoreNull(reservedFields,mixpanelAppSyncConfig.getEventName());
        CollectionUtils.addIgnoreNull(reservedFields,mixpanelAppSyncConfig.getEventTimeStamp());
        CollectionUtils.addIgnoreNull(reservedFields,mixpanelAppSyncConfig.getDistinctIDForEvent());
        return reservedFields;
    }*/
}
