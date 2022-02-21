package io.castled.apps.connectors.mixpanel;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.ExternalAppType;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.GenericSyncObject;
import io.castled.apps.models.PrimaryKeyEligibles;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.schema.ParameterFieldDTO;
import io.castled.schema.SchemaFieldDTO;
import io.castled.schema.mapping.*;
import io.castled.schema.models.SchemaType;

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
        List<MappingGroup> mappingGroups = Lists.newArrayList();

        PrimaryKeyGroup primaryKeyGroup = new PrimaryKeyGroup();
        List<SchemaFieldDTO> primaryKeys = Lists.newArrayList();
        primaryKeys.add(new SchemaFieldDTO(MixpanelObjectFields.GROUP_PROFILE_FIELDS.GROUP_ID.getFieldName(),
                SchemaType.STRING.getDisplayName(), false));
        primaryKeyGroup.setPrimaryKeys(primaryKeys);
        mappingGroups.add(primaryKeyGroup);

        MiscellaneousFieldGroup miscellaneousFieldGroup = new MiscellaneousFieldGroup();
        mappingGroups.add(miscellaneousFieldGroup);

        return mappingGroups;
    }

    private List<MappingGroup> getMappingGroupsForUserProfileObject(MixpanelAppConfig config, MixpanelAppSyncConfig mixpanelAppSyncConfig) {
        List<MappingGroup> mappingGroups = Lists.newArrayList();

        //Primary Key Section
        PrimaryKeyGroup primaryKeyGroup = new PrimaryKeyGroup();
        List<SchemaFieldDTO> primaryKeys = Lists.newArrayList();
        primaryKeys.add(new SchemaFieldDTO(MixpanelObjectFields.USER_PROFILE_FIELDS.DISTINCT_ID.getFieldName(),
                SchemaType.STRING.getDisplayName(), false));
        primaryKeyGroup.setPrimaryKeys(primaryKeys);
        mappingGroups.add(primaryKeyGroup);

        //Destination field group
        DestinationFieldGroup destinationFieldGroup = new DestinationFieldGroup();
        List<SchemaFieldDTO> optionalFields = Lists.newArrayList();
        optionalFields.add(new SchemaFieldDTO(MixpanelObjectFields.USER_PROFILE_FIELDS.FIRST_NAME.getFieldName(), SchemaType.STRING.getDisplayName(), true));
        optionalFields.add(new SchemaFieldDTO(MixpanelObjectFields.USER_PROFILE_FIELDS.LAST_NAME.getFieldName(), SchemaType.STRING.getDisplayName(), true));
        destinationFieldGroup.setOptionalFields(optionalFields);
        List<SchemaFieldDTO> mandatoryFields = Lists.newArrayList();
        mandatoryFields.add(new SchemaFieldDTO(MixpanelObjectFields.USER_PROFILE_FIELDS.EMAIL.getFieldName(), SchemaType.STRING.getDisplayName(), false));
        destinationFieldGroup.setMandatoryFields(mandatoryFields);
        mappingGroups.add(destinationFieldGroup);

        //Misc group
        MiscellaneousFieldGroup miscellaneousFieldGroup = new MiscellaneousFieldGroup();
        mappingGroups.add(miscellaneousFieldGroup);

        return mappingGroups;
    }

    private List<MappingGroup> getMappingGroupsForEventObject(MixpanelAppConfig config, MixpanelAppSyncConfig mixpanelAppSyncConfig) {

        List<MappingGroup> mappingGroups = Lists.newArrayList();

        //Important params group
        ImportantParameterGroup importantParameterGroup = new ImportantParameterGroup();
        List<ParameterFieldDTO> importantParameters = Lists.newArrayList();
        importantParameters.add(new ParameterFieldDTO("Column identifying the user associated with the event",
                "Column identifying the user associated with the event",
                MixpanelObjectFields.EVENT_FIELDS.DISTINCT_ID.getFieldName(), SchemaType.STRING.getDisplayName(), false));
        importantParameters.add(new ParameterFieldDTO("Column identifying Event Timestamp",
                "If not mentioned system will default to the message processing time",
                MixpanelObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldName(), SchemaType.TIMESTAMP.getDisplayName(), true));
        importantParameterGroup.setFields(importantParameters);
        mappingGroups.add(importantParameterGroup);

        //Misc group
        MiscellaneousFieldGroup miscellaneousFieldGroup = new MiscellaneousFieldGroup();
        mappingGroups.add(miscellaneousFieldGroup);

        return mappingGroups;
    }
/*
    public PipelineConfigDTO validateAndEnrichPipelineConfig(PipelineConfigDTO pipelineConfig) throws BadRequestException {
        MixpanelAppSyncConfig mixpanelAppSyncConfig = (MixpanelAppSyncConfig) pipelineConfig.getAppSyncConfig();
        String objectName = ((MixpanelAppSyncConfig)pipelineConfig.getAppSyncConfig()).getObject().getObjectName();

        if(MixpanelObject.EVENT.getName().equalsIgnoreCase(objectName)) {
            enrichPipelineConfigForEventObject(pipelineConfig, mixpanelAppSyncConfig);
        }
        if(MixpanelObject.USER_PROFILE.getName().equalsIgnoreCase(objectName)){
            enrichPipelineConfigForUserProfileObject(pipelineConfig, mixpanelAppSyncConfig);
        }
        if(MixpanelObject.GROUP_PROFILE.getName().equalsIgnoreCase(objectName)){
            enrichPipelineConfigForGroupProfileObject(pipelineConfig, mixpanelAppSyncConfig);
        }
        return pipelineConfig;
    }

    private void enrichPipelineConfigForUserProfileObject(PipelineConfigDTO pipelineConfig, MixpanelAppSyncConfig mixpanelAppSyncConfig) throws BadRequestException{

        String distinctID = Optional.ofNullable(mixpanelAppSyncConfig.getDistinctID()).orElseThrow(()->new BadRequestException("Column uniquely identifying the User is mandatory"));

        List<FieldMapping> additionalMapping = Lists.newArrayList();
        Optional.ofNullable(distinctID).ifPresent((ID) -> additionalMapping.add(new FieldMapping(ID,MixpanelObjectFields.USER_PROFILE_FIELDS.DISTINCT_ID.getFieldName(),false)));
        Optional.ofNullable(mixpanelAppSyncConfig.getLastName()).ifPresent(lastName -> additionalMapping.add(new FieldMapping(lastName,MixpanelObjectFields.USER_PROFILE_FIELDS.LAST_NAME.getFieldName(),false)));
        Optional.ofNullable(mixpanelAppSyncConfig.getFirstName()).ifPresent((firstName) -> additionalMapping.add(new FieldMapping(firstName,MixpanelObjectFields.USER_PROFILE_FIELDS.FIRST_NAME.getFieldName(),false)));
        Optional.ofNullable(mixpanelAppSyncConfig.getUserEmail()).ifPresent((email) -> additionalMapping.add(new FieldMapping(email,MixpanelObjectFields.USER_PROFILE_FIELDS.EMAIL.getFieldName(),false)));
        DataMappingUtils.addAdditionalMappings((TargetFieldsMapping) pipelineConfig.getMapping(), additionalMapping);

        pipelineConfig.getMapping().setPrimaryKeys(Collections.singletonList(MixpanelObjectFields.USER_PROFILE_FIELDS.DISTINCT_ID.getFieldName()));
    }

    private void enrichPipelineConfigForGroupProfileObject(PipelineConfigDTO pipelineConfig, MixpanelAppSyncConfig mixpanelAppSyncConfig) throws BadRequestException{
        String groupID = Optional.ofNullable(mixpanelAppSyncConfig.getGroupID()).orElseThrow(()->new BadRequestException("Column uniquely identifying the Group is mandatory"));
        String groupKey = Optional.ofNullable(mixpanelAppSyncConfig.getGroupKey()).orElseThrow(()->new BadRequestException("Group key is mandatory"));

        List<FieldMapping> additionalMapping = Lists.newArrayList();
        Optional.ofNullable(groupID).ifPresent((ID) -> additionalMapping.add(new FieldMapping(ID,MixpanelObjectFields.GROUP_PROFILE_FIELDS.GROUP_ID.getFieldName(),false)));
        DataMappingUtils.addAdditionalMappings((TargetFieldsMapping) pipelineConfig.getMapping(), additionalMapping);

        pipelineConfig.getMapping().setPrimaryKeys(Collections.singletonList(MixpanelObjectFields.GROUP_PROFILE_FIELDS.GROUP_ID.getFieldName()));
    }

    private void enrichPipelineConfigForEventObject(PipelineConfigDTO pipelineConfig, MixpanelAppSyncConfig mixpanelAppSyncConfig) throws BadRequestException{

        String eventId = Optional.ofNullable(mixpanelAppSyncConfig.getEventID()).orElseThrow(()->new BadRequestException("Column uniquely identifying the Event is mandatory"));
        Optional.ofNullable(mixpanelAppSyncConfig.getEventName()).orElseThrow(()-> new BadRequestException("Event Name is mandatory"));

        List<FieldMapping> additionalMapping = Lists.newArrayList();
        Optional.ofNullable(eventId).ifPresent((insertID) -> additionalMapping
                .add(new FieldMapping(insertID,MixpanelObjectFields.EVENT_FIELDS.INSERT_ID.getFieldName(),false)));
        Optional.ofNullable(mixpanelAppSyncConfig.getDistinctIDForEvent()).ifPresent((distinctID) -> additionalMapping
                .add(new FieldMapping(distinctID,MixpanelObjectFields.EVENT_FIELDS.DISTINCT_ID.getFieldName(),false)));
        Optional.ofNullable(mixpanelAppSyncConfig.getEventIP()).ifPresent((eventIP) -> additionalMapping
                .add(new FieldMapping(eventIP,MixpanelObjectFields.EVENT_FIELDS.GEO_IP.getFieldName(),false)));
        Optional.ofNullable(mixpanelAppSyncConfig.getEventTimeStamp()).ifPresent((eventTimeStamp) -> additionalMapping
                .add(new FieldMapping(eventTimeStamp,MixpanelObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldName(),false)));
        DataMappingUtils.addAdditionalMappings((TargetFieldsMapping) pipelineConfig.getMapping(), additionalMapping);

        pipelineConfig.getMapping().setPrimaryKeys(Collections.singletonList(MixpanelObjectFields.EVENT_FIELDS.INSERT_ID.getFieldName()));
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
