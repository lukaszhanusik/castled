package io.castled.apps.connectors.customerio;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.ExternalAppType;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.GenericSyncObject;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.mapping.PrimaryKeyGroupField;
import io.castled.mapping.QuestionnaireGroupField;
import io.castled.schema.mapping.MappingGroup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class CustomerIOAppConnector implements ExternalAppConnector<CustomerIOAppConfig,
        CustomerIODataWriter, CustomerIOAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(CustomerIOAppConfig config, CustomerIOAppSyncConfig customerIOAppSyncConfig) {
        return Arrays.stream(CustomerIOObject.values()).map(customerIOObject -> new FormFieldOption(new GenericSyncObject(customerIOObject.getName(),
                ExternalAppType.CUSTOMERIO), customerIOObject.getName())).collect(Collectors.toList());
    }

    @Override
    public CustomerIODataWriter getDataSink() {
        return ObjectRegistry.getInstance(CustomerIODataWriter.class);
    }

    @Override
    public ExternalAppSchema getSchema(CustomerIOAppConfig config, CustomerIOAppSyncConfig customerIOAppSyncConfig) {
        return new ExternalAppSchema(null);
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

        List<PrimaryKeyGroupField> primaryKeyGroupFields = Lists.newArrayList();
        primaryKeyGroupFields.add(PrimaryKeyGroupField.builder()
                .name(CustomerIOObjectFields.CONTACTS_FIELDS.ID.getFieldName())
                .displayName(CustomerIOObjectFields.CONTACTS_FIELDS.ID.getFieldTitle())
                .build());

        primaryKeyGroupFields.add(PrimaryKeyGroupField.builder()
                .name(CustomerIOObjectFields.CONTACTS_FIELDS.EMAIL.getFieldName())
                .displayName(CustomerIOObjectFields.CONTACTS_FIELDS.EMAIL.getFieldTitle())
                .build());

        MappingGroupAggregator.Builder builder = MappingGroupAggregator.builder();
        return builder.addPrimaryKeyFields(primaryKeyGroupFields).addElasticAppFields(false, false).build().getMappingGroups();
    }

    private List<MappingGroup> getMappingGroupsForEventObject(CustomerIOAppConfig config, CustomerIOAppSyncConfig customerIOAppSyncConfig) {
        String eventType = customerIOAppSyncConfig.getEventType();

        List<QuestionnaireGroupField> questionnaireGroupFields = Lists.newArrayList();
        if (CIOEventTypeEnum.TRACK_EVENT.getEventType().equalsIgnoreCase(eventType)) {
            questionnaireGroupFields.add(QuestionnaireGroupField.builder()
                    .title("Column identifying Customer.io ID of the person")
                    .description("This field will be used to uniquely identify the person associated with the event")
                    .name(CustomerIOObjectFields.EVENT_FIELDS.CUSTOMER_ID.getFieldName())
                    .optional(false)
                    .build());

            questionnaireGroupFields.add(QuestionnaireGroupField.builder()
                    .title("Column identifying the Event Timestamp")
                    .description("If selected this will be used as the event timestamp, else API will default the time event reaches the server")
                    .name(CustomerIOObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldName())
                    .optional(true)
                    .build());

        }
        if (CIOEventTypeEnum.TRACK_PAGE_VIEWS.getEventType().equalsIgnoreCase(eventType)) {
            questionnaireGroupFields.add(QuestionnaireGroupField.builder()
                    .title("Column identifying the URL of the page viewed")
                    .description("Column identifying the URL of the page viewed")
                    .name(CustomerIOObjectFields.EVENT_FIELDS.PAGE_URL.getFieldName())
                    .optional(false)
                    .build());

            questionnaireGroupFields.add(QuestionnaireGroupField.builder()
                    .title("Column identifying Customer.io id (customer_id) of the person")
                    .description("This field will be used to uniquely identify the person associated with the event")
                    .name(CustomerIOObjectFields.EVENT_FIELDS.CUSTOMER_ID.getFieldName())
                    .optional(false)
                    .build());

            questionnaireGroupFields.add(QuestionnaireGroupField.builder()
                    .title("Column identifying the Event Timestamp")
                    .description("If selected this will be used as the event timestamp else API will default the time event reaches the server")
                    .name(CustomerIOObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldName())
                    .optional(true)
                    .build());

        }

        MappingGroupAggregator.Builder builder = MappingGroupAggregator.builder();
        return builder.addQuestionnaireFields(questionnaireGroupFields).addElasticAppFields(false, false).build().getMappingGroups();
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
}
