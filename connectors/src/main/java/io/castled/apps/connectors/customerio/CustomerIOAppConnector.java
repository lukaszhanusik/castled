package io.castled.apps.connectors.customerio;

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
import io.castled.schema.mapping.ImportantParameterGroup;
import io.castled.schema.mapping.MappingGroup;
import io.castled.schema.mapping.MiscellaneousFieldGroup;
import io.castled.schema.mapping.PrimaryKeyGroup;
import io.castled.schema.models.SchemaType;

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

        List<MappingGroup> mappingGroups = Lists.newArrayList();

        PrimaryKeyGroup primaryKeyGroup = new PrimaryKeyGroup();
        List<SchemaFieldDTO> primaryKeys = Lists.newArrayList();
        primaryKeys.add(new SchemaFieldDTO(CustomerIOObjectFields.CONTACTS_FIELDS.ID.getFieldName(),
                SchemaType.STRING.getDisplayName(), false));
        primaryKeys.add(new SchemaFieldDTO(CustomerIOObjectFields.CONTACTS_FIELDS.EMAIL.getFieldName(),
                SchemaType.STRING.getDisplayName(), false));
        primaryKeyGroup.setPrimaryKeys(primaryKeys);
        mappingGroups.add(primaryKeyGroup);

        MiscellaneousFieldGroup miscellaneousFieldGroup = new MiscellaneousFieldGroup();
        mappingGroups.add(miscellaneousFieldGroup);

        return mappingGroups;
    }

    private List<MappingGroup> getMappingGroupsForEventObject(CustomerIOAppConfig config, CustomerIOAppSyncConfig customerIOAppSyncConfig) {
        List<MappingGroup> mappingGroups = Lists.newArrayList();


        String eventType = customerIOAppSyncConfig.getEventType();
        ImportantParameterGroup importantParameterGroup = new ImportantParameterGroup();
        List<ParameterFieldDTO> importantParameters = Lists.newArrayList();
        if (CIOEventTypeEnum.TRACK_EVENT.getEventType().equalsIgnoreCase(eventType)) {
            importantParameters.add(new ParameterFieldDTO("Column identifying Customer.io id (customer_id) of the person",
                    "Column identifying Customer.io id (customer_id) of the person",
                    CustomerIOObjectFields.EVENT_FIELDS.CUSTOMER_ID.getFieldName(), SchemaType.STRING.getDisplayName(), false));

            importantParameters.add(new ParameterFieldDTO("Column identifying the Event Timestamp",
                    "Column identifying the Event Timestamp",
                    CustomerIOObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldName(), SchemaType.STRING.getDisplayName(), false));
        }
        if (CIOEventTypeEnum.TRACK_PAGE_VIEWS.getEventType().equalsIgnoreCase(eventType)) {

            importantParameters.add(new ParameterFieldDTO("Column identifying the URL of the page viewed",
                    "Column identifying the URL of the page viewed",
                    CustomerIOObjectFields.EVENT_FIELDS.PAGE_URL.getFieldName(), SchemaType.STRING.getDisplayName(), false));

            importantParameters.add(new ParameterFieldDTO("Column identifying Customer.io id (customer_id) of the person",
                    "Column identifying Customer.io id (customer_id) of the person",
                    CustomerIOObjectFields.EVENT_FIELDS.CUSTOMER_ID.getFieldName(), SchemaType.STRING.getDisplayName(), false));

            importantParameters.add(new ParameterFieldDTO("Column identifying the Event Timestamp",
                    "Column identifying the Event Timestamp",
                    CustomerIOObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldName(), SchemaType.STRING.getDisplayName(), false));
        }
        importantParameterGroup.setFields(importantParameters);
        mappingGroups.add(importantParameterGroup);

        MiscellaneousFieldGroup miscellaneousFieldGroup = new MiscellaneousFieldGroup();
        mappingGroups.add(miscellaneousFieldGroup);

        return mappingGroups;
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

        FieldMapping primaryKeyFieldMapping = pipelineConfig.getMapping().getFieldMappings().stream().filter(fieldMapping -> fieldMapping.getWarehouseField().equalsIgnoreCase(personIdentifier))
                .findFirst().orElseThrow(() -> new BadRequestException("Mapping missing for the Destination App primary key"));

        if (CustomerIOObjectFields.CONTACTS_FIELDS.EMAIL.getFieldName().equalsIgnoreCase(pk)) {
            primaryKeyFieldMapping.setAppField(CustomerIOObjectFields.CONTACTS_FIELDS.EMAIL.getFieldName());
            pipelineConfig.getMapping().setPrimaryKeys(Collections.singletonList(CustomerIOObjectFields.CONTACTS_FIELDS.EMAIL.getFieldName()));
        }
        if (CustomerIOObjectFields.CONTACTS_FIELDS.ID.getFieldName().equalsIgnoreCase(pk)) {
            primaryKeyFieldMapping.setAppField(CustomerIOObjectFields.CONTACTS_FIELDS.ID.getFieldName());
            pipelineConfig.getMapping().setPrimaryKeys(Collections.singletonList(CustomerIOObjectFields.CONTACTS_FIELDS.ID.getFieldName()));
        }
    }

    private void enrichPipelineConfigForEventObject(PipelineConfigDTO pipelineConfig, CustomerIOAppSyncConfig customerIOAppSyncConfig) throws BadRequestException {
        FieldMapping primaryKeyFieldMapping = pipelineConfig.getMapping().getFieldMappings().stream().filter(fieldMapping -> fieldMapping.getWarehouseField()
                .equalsIgnoreCase(customerIOAppSyncConfig.getEventId())).findFirst().orElseThrow(() -> new BadRequestException("Mapping missing for the Destination App primary key"));
        primaryKeyFieldMapping.setAppField(CustomerIOObjectFields.EVENT_FIELDS.EVENT_ID.getFieldName());
        String eventType = Optional.ofNullable(customerIOAppSyncConfig.getEventType()).orElseThrow(() -> new BadRequestException("Event type is mandatory"));

        if (CIOEventTypeEnum.TRACK_EVENT.getEventType().equalsIgnoreCase(eventType)) {
            pipelineConfig.getMapping().getFieldMappings().stream().filter(fieldMapping -> fieldMapping.getWarehouseField()
                            .equalsIgnoreCase(Optional.ofNullable(customerIOAppSyncConfig.getEventName()).orElseThrow(() -> new BadRequestException("Event Name is mandatory for Events"))))
                    .findFirst().ifPresent(fieldMapping -> fieldMapping.setAppField(CustomerIOObjectFields.EVENT_FIELDS.EVENT_NAME.getFieldName()));
        }
        if (CIOEventTypeEnum.TRACK_PAGE_VIEWS.getEventType().equalsIgnoreCase(eventType)) {
            pipelineConfig.getMapping().getFieldMappings().stream().filter(fieldMapping -> fieldMapping.getWarehouseField()
                            .equalsIgnoreCase(Optional.ofNullable(customerIOAppSyncConfig.getPageURL()).orElseThrow(() -> new BadRequestException("Page View is mandatory for Page View Events"))))
                    .findFirst().ifPresent(fieldMapping -> fieldMapping.setAppField(CustomerIOObjectFields.EVENT_FIELDS.PAGE_URL.getFieldName()));
        }

        pipelineConfig.getMapping().setPrimaryKeys(Collections.singletonList(CustomerIOObjectFields.EVENT_FIELDS.EVENT_ID.getFieldName()));
        pipelineConfig.getMapping().getFieldMappings().stream()
                .filter(fieldMapping -> fieldMapping.getWarehouseField().equalsIgnoreCase(customerIOAppSyncConfig.getCustomerId()))
                .findFirst().ifPresent(fieldMapping -> fieldMapping.setAppField(CustomerIOObjectFields.EVENT_FIELDS.CUSTOMER_ID.getFieldName()));
        pipelineConfig.getMapping().getFieldMappings().stream()
                .filter(fieldMapping -> fieldMapping.getWarehouseField().equalsIgnoreCase(customerIOAppSyncConfig.getEventTimestamp()))
                .findFirst().ifPresent(fieldMapping -> fieldMapping.setAppField(CustomerIOObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldName()));
    }*/
}
