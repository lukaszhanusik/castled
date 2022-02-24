package io.castled.apps.connectors.customerio;

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
public class CustomerIOAppConnector implements ExternalAppConnector<CustomerIOAppConfig,
        CustomerIODataSink, CustomerIOAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(CustomerIOAppConfig config, CustomerIOAppSyncConfig customerIOAppSyncConfig) {
        return Arrays.stream(CustomerIOObject.values()).map(customerIOObject -> new FormFieldOption(new GenericSyncObject(customerIOObject.getName(),
                ExternalAppType.CUSTOMERIO), customerIOObject.getName())).collect(Collectors.toList());
    }

    @Override
    public CustomerIODataSink getDataSink() {
        return ObjectRegistry.getInstance(CustomerIODataSink.class);
    }

    @Override
    public ExternalAppSchema getSchema(CustomerIOAppConfig config, CustomerIOAppSyncConfig customerIOAppSyncConfig) {
        return new ExternalAppSchema(null, PrimaryKeyEligibles.autoDetect());
    }

    public List<MappingGroup> getMappingGroups(CustomerIOAppConfig config, CustomerIOAppSyncConfig customerIOAppSyncConfig) {
        String object = customerIOAppSyncConfig.getObject().getObjectName();
        if (CustomerIOObject.EVENT.getName().equalsIgnoreCase(object)) {
            return getMappingGroupsForEventObject(config, customerIOAppSyncConfig);
        }
        if (CustomerIOObject.PERSON.getName().equalsIgnoreCase(object)) {
            return getMappingGroupsForPersonObject(config, customerIOAppSyncConfig);
        }
        return null;
    }

    private List<MappingGroup> getMappingGroupsForPersonObject(CustomerIOAppConfig config, CustomerIOAppSyncConfig customerIOAppSyncConfig) {

        List<AppFieldDetails> primaryKeys = Lists.newArrayList();
        primaryKeys.add(AppFieldDetails.builder()
                .internalName(CustomerIOObjectFields.CONTACTS_FIELDS.ID.getFieldName())
                .displayName(CustomerIOObjectFields.CONTACTS_FIELDS.ID.getFieldTitle())
                .type(SchemaType.STRING.getDisplayName())
                .optional(true)
                .build());

        primaryKeys.add(AppFieldDetails.builder()
                .internalName(CustomerIOObjectFields.CONTACTS_FIELDS.EMAIL.getFieldName())
                .displayName(CustomerIOObjectFields.CONTACTS_FIELDS.EMAIL.getFieldTitle())
                .type(SchemaType.STRING.getDisplayName())
                .optional(true)
                .build());

        List<MappingGroup> mappingGroups = Lists.newArrayList();
        mappingGroups.add(MappingGroupUtil.constructPrimaryKeyGroup(primaryKeys));
        mappingGroups.add(MappingGroupUtil.constructMiscellaneousFieldGroup(false));

        MappingGroupAggregator.Builder builder = MappingGroupAggregator.builder();
        return builder.addPrimaryKeys(primaryKeys).addMiscellaneousGroup(false).build().getMappingGroups();

        //return mappingGroups;
    }

    private List<MappingGroup> getMappingGroupsForEventObject(CustomerIOAppConfig config, CustomerIOAppSyncConfig customerIOAppSyncConfig) {
        String eventType = customerIOAppSyncConfig.getEventType();

        List<AppFieldDetails> importantParameters = Lists.newArrayList();
        if (CIOEventTypeEnum.TRACK_EVENT.getEventType().equalsIgnoreCase(eventType)) {

            importantParameters.add(AppFieldDetails.builder()
                    .title("Column identifying Customer.io id (customer_id) of the person")
                    .description("This field will be used to uniquely identifying the person associated with the event")
                    .internalName(CustomerIOObjectFields.EVENT_FIELDS.CUSTOMER_ID.getFieldName())
                    .displayName(CustomerIOObjectFields.EVENT_FIELDS.CUSTOMER_ID.getFieldTitle())
                    .type(SchemaType.STRING.getDisplayName())
                    .optional(false)
                    .build());

            importantParameters.add(AppFieldDetails.builder()
                    .title("Column identifying the Event Timestamp")
                    .description("If selected this field will be used as the event timestamp else API will default the time event reaches the server")
                    .internalName(CustomerIOObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldName())
                    .displayName(CustomerIOObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldTitle())
                    .type(SchemaType.STRING.getDisplayName())
                    .optional(false)
                    .build());

        }
        if (CIOEventTypeEnum.TRACK_PAGE_VIEWS.getEventType().equalsIgnoreCase(eventType)) {

            importantParameters.add(AppFieldDetails.builder()
                    .title("Column identifying the URL of the page viewed")
                    .description("Column identifying the URL of the page viewed")
                    .internalName(CustomerIOObjectFields.EVENT_FIELDS.PAGE_URL.getFieldName())
                    .displayName(CustomerIOObjectFields.EVENT_FIELDS.PAGE_URL.getFieldTitle())
                    .type(SchemaType.STRING.getDisplayName())
                    .optional(false)
                    .build());

            importantParameters.add(AppFieldDetails.builder()
                    .title("Column identifying Customer.io id (customer_id) of the person")
                    .description("This field will be used to uniquely identifying the person associated with the event")
                    .internalName(CustomerIOObjectFields.EVENT_FIELDS.CUSTOMER_ID.getFieldName())
                    .displayName(CustomerIOObjectFields.EVENT_FIELDS.CUSTOMER_ID.getFieldTitle())
                    .type(SchemaType.STRING.getDisplayName())
                    .optional(false)
                    .build());

            importantParameters.add(AppFieldDetails.builder()
                    .title("Column identifying the Event Timestamp")
                    .description("If selected this field will be used as the event timestamp else API will default the time event reaches the server")
                    .internalName(CustomerIOObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldName())
                    .displayName(CustomerIOObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldTitle())
                    .type(SchemaType.STRING.getDisplayName())
                    .optional(false)
                    .build());

        }

        List<MappingGroup> mappingGroups = Lists.newArrayList();
        mappingGroups.add(MappingGroupUtil.constructImportantParameterGroup(importantParameters));
        mappingGroups.add(MappingGroupUtil.constructMiscellaneousFieldGroup(false));

        MappingGroupAggregator.Builder builder = MappingGroupAggregator.builder();
        return builder.addImportantParameters(importantParameters).addMiscellaneousGroup(false).build().getMappingGroups();

        //return mappingGroups;
    }

    public List<AppSyncMode> getSyncModes(CustomerIOAppConfig config, CustomerIOAppSyncConfig customerIOAppSyncConfig) {
        String object = customerIOAppSyncConfig.getObject().getObjectName();
        if (CustomerIOObject.EVENT.getName().equalsIgnoreCase(object)) {
            return Lists.newArrayList(AppSyncMode.INSERT);
        }
        if (CustomerIOObject.PERSON.getName().equalsIgnoreCase(object)) {
            return Lists.newArrayList(AppSyncMode.UPSERT);
        }
        return Lists.newArrayList(AppSyncMode.INSERT, AppSyncMode.UPSERT, AppSyncMode.UPDATE);
    }

    public Class<CustomerIOAppSyncConfig> getMappingConfigType() {
        return CustomerIOAppSyncConfig.class;
    }

    @Override
    public Class<CustomerIOAppConfig> getAppConfigType() {
        return CustomerIOAppConfig.class;
    }

/*    public PipelineConfigDTO validateAndEnrichPipelineConfig(PipelineConfigDTO pipelineConfig) throws BadRequestException {
        CustomerIOAppSyncConfig customerIOAppSyncConfig = (CustomerIOAppSyncConfig) pipelineConfig.getAppSyncConfig();
        String objectName = ((CustomerIOAppSyncConfig) pipelineConfig.getAppSyncConfig()).getObject().getObjectName();

        if (CustomerIOObject.EVENT.getName().equalsIgnoreCase(objectName)) {
            enrichPipelineConfigForEventObject(pipelineConfig, customerIOAppSyncConfig);
        }
        if (CustomerIOObject.PERSON.getName().equalsIgnoreCase(objectName)) {
            enrichPipelineConfigForPersonObject(pipelineConfig, customerIOAppSyncConfig);
        }
        return pipelineConfig;
    }*/

/*    private void enrichPipelineConfigForPersonObject(PipelineConfigDTO pipelineConfig, CustomerIOAppSyncConfig customerIOAppSyncConfig) throws BadRequestException {
        String pk = Optional.ofNullable(customerIOAppSyncConfig.getPrimaryKey()).orElseThrow(() -> new BadRequestException("Primary key for the Destination App Record is mandatory"));
        String personIdentifier = Optional.ofNullable(customerIOAppSyncConfig.getPersonIdentifier()).orElseThrow(() -> new BadRequestException("Column uniquely identifying the Person Records is mandatory"));

        FieldMapping primaryKeyFieldMapping = ((TargetFieldsMapping)pipelineConfig.getMapping()).getFieldMappings().stream().filter(fieldMapping -> fieldMapping.getWarehouseField().equalsIgnoreCase(personIdentifier))
                .findFirst().orElseThrow(() -> new BadRequestException("Mapping missing for the Destination App primary key"));

        if (CustomerIOObjectFields.CONTACTS_FIELDS.EMAIL.getInternalName().equalsIgnoreCase(pk)) {
            primaryKeyFieldMapping.setAppField(CustomerIOObjectFields.CONTACTS_FIELDS.EMAIL.getInternalName());
            pipelineConfig.getMapping().setPrimaryKeys(Collections.singletonList(CustomerIOObjectFields.CONTACTS_FIELDS.EMAIL.getInternalName()));
        }
        if (CustomerIOObjectFields.CONTACTS_FIELDS.ID.getInternalName().equalsIgnoreCase(pk)) {
            primaryKeyFieldMapping.setAppField(CustomerIOObjectFields.CONTACTS_FIELDS.ID.getInternalName());
            pipelineConfig.getMapping().setPrimaryKeys(Collections.singletonList(CustomerIOObjectFields.CONTACTS_FIELDS.ID.getInternalName()));
        }
    }

    private void enrichPipelineConfigForEventObject(PipelineConfigDTO pipelineConfig, CustomerIOAppSyncConfig customerIOAppSyncConfig) throws BadRequestException {
        FieldMapping primaryKeyFieldMapping =  ((TargetFieldsMapping)pipelineConfig.getMapping()).getFieldMappings().stream().filter(fieldMapping -> fieldMapping.getWarehouseField()
                .equalsIgnoreCase(customerIOAppSyncConfig.getEventId())).findFirst().orElseThrow(() -> new BadRequestException("Mapping missing for the Destination App primary key"));
        primaryKeyFieldMapping.setAppField(CustomerIOObjectFields.EVENT_FIELDS.EVENT_ID.getInternalName());
        String eventType = Optional.ofNullable(customerIOAppSyncConfig.getEventType()).orElseThrow(() -> new BadRequestException("Event type is mandatory"));

<<<<<<< HEAD
        if (CIOEventTypeEnum.TRACK_EVENT.getEventType().equalsIgnoreCase(eventType)) {
            pipelineConfig.getMapping().getFieldMappings().stream().filter(fieldMapping -> fieldMapping.getWarehouseField()
                            .equalsIgnoreCase(Optional.ofNullable(customerIOAppSyncConfig.getEventName()).orElseThrow(() -> new BadRequestException("Event Name is mandatory for Events"))))
                    .findFirst().ifPresent(fieldMapping -> fieldMapping.setAppField(CustomerIOObjectFields.EVENT_FIELDS.EVENT_NAME.getInternalName()));
        }
        if (CIOEventTypeEnum.TRACK_PAGE_VIEWS.getEventType().equalsIgnoreCase(eventType)) {
            pipelineConfig.getMapping().getFieldMappings().stream().filter(fieldMapping -> fieldMapping.getWarehouseField()
=======
        if ("event".equalsIgnoreCase(eventType)) {
            ((TargetFieldsMapping)pipelineConfig.getMapping()).getFieldMappings().stream().filter(fieldMapping -> fieldMapping.getWarehouseField()
                            .equalsIgnoreCase(Optional.ofNullable(customerIOAppSyncConfig.getEventName()).orElseThrow(() -> new BadRequestException("Event Name is mandatory for Events"))))
                    .findFirst().ifPresent(fieldMapping -> fieldMapping.setAppField(CustomerIOObjectFields.EVENT_FIELDS.EVENT_NAME.getInternalName()));
        }
        if ("pageView".equalsIgnoreCase(eventType)) {
            ((TargetFieldsMapping)pipelineConfig.getMapping()).getFieldMappings().stream().filter(fieldMapping -> fieldMapping.getWarehouseField()
>>>>>>> db16da0c148b2c0fed4cffe07107bd5929f5f7d8
                            .equalsIgnoreCase(Optional.ofNullable(customerIOAppSyncConfig.getPageURL()).orElseThrow(() -> new BadRequestException("Page View is mandatory for Page View Events"))))
                    .findFirst().ifPresent(fieldMapping -> fieldMapping.setAppField(CustomerIOObjectFields.EVENT_FIELDS.PAGE_URL.getInternalName()));
        }

        pipelineConfig.getMapping().setPrimaryKeys(Collections.singletonList(CustomerIOObjectFields.EVENT_FIELDS.EVENT_ID.getInternalName()));
        ((TargetFieldsMapping)pipelineConfig.getMapping()).getFieldMappings().stream()
                .filter(fieldMapping -> fieldMapping.getWarehouseField().equalsIgnoreCase(customerIOAppSyncConfig.getCustomerId()))
                .findFirst().ifPresent(fieldMapping -> fieldMapping.setAppField(CustomerIOObjectFields.EVENT_FIELDS.CUSTOMER_ID.getInternalName()));
        ((TargetFieldsMapping)pipelineConfig.getMapping()).getFieldMappings().stream()
                .filter(fieldMapping -> fieldMapping.getWarehouseField().equalsIgnoreCase(customerIOAppSyncConfig.getEventTimestamp()))
                .findFirst().ifPresent(fieldMapping -> fieldMapping.setAppField(CustomerIOObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getInternalName()));
    }*/
}
