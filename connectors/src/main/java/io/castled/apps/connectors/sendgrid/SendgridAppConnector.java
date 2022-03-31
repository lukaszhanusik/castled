package io.castled.apps.connectors.sendgrid;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.ExternalAppType;

import io.castled.apps.connectors.sendgrid.dtos.ContactAttributesResponse;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.GenericSyncObject;
import io.castled.apps.connectors.sendgrid.dtos.ContactAttribute;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.mapping.FixedGroupAppField;
import io.castled.mapping.PrimaryKeyGroupField;
import io.castled.schema.mapping.MappingGroup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class SendgridAppConnector implements ExternalAppConnector<SendgridAppConfig, SendgridDataWriter,
        SendgridAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(SendgridAppConfig config, SendgridAppSyncConfig mappingConfig) {
        return Arrays.stream(SendgridObject.values()).map(sendgridObject -> new FormFieldOption(new GenericSyncObject(sendgridObject.getName(),
                ExternalAppType.SENDGRID), sendgridObject.getName())).collect(Collectors.toList());
    }

    @Override
    public SendgridDataWriter getDataSink() { return ObjectRegistry.getInstance(SendgridDataWriter.class); }

    @Override
    public ExternalAppSchema getSchema(SendgridAppConfig config, SendgridAppSyncConfig mappingConfig) {
        SendgridRestClient sendgridRestClient = new SendgridRestClient(config);
        SendgridObject sendgridObject = SendgridObject.getObjectByName(mappingConfig.getObject().getObjectName());
        ContactAttributesResponse contactAttributes = sendgridRestClient.getContactAttributes();
        return new ExternalAppSchema(SendgridUtils.getSchema(sendgridObject, contactAttributes));
    }

    @Override
    public Class<SendgridAppSyncConfig> getMappingConfigType() {
        return SendgridAppSyncConfig.class;
    }

    @Override
    public List<MappingGroup> getMappingGroups(SendgridAppConfig config, SendgridAppSyncConfig sendgridAppSyncConfig) {
        // Primary key group
        List<PrimaryKeyGroupField> primaryKeyGroupFields = Lists.newArrayList();
        primaryKeyGroupFields.add(PrimaryKeyGroupField.builder()
                .name(ContactAttribute.EMAIL)
                .displayName(ContactAttribute.EMAIL)
                .optional(false)
                .build());

        // Fixed field group
        SendgridRestClient sendgridRestClient = new SendgridRestClient(config);
        ContactAttributesResponse contactAttributes = sendgridRestClient.getContactAttributes();
        List<FixedGroupAppField> fixedGroupAppFields = contactAttributes.getReservedFields().stream()
                .filter(attr -> !ContactAttribute.EMAIL.equals(attr.getName()))
                .map(attr -> new FixedGroupAppField(attr.getName(), attr.getName(), true))
                .collect(Collectors.toList());
        fixedGroupAppFields.addAll(contactAttributes.getCustomFields().stream()
                .map(attr -> new FixedGroupAppField(attr.getName(), attr.getName(), true))
                .collect(Collectors.toList()));

        return MappingGroupAggregator.builder()
                .addPrimaryKeyFields(primaryKeyGroupFields)
                .addFixedAppFields(fixedGroupAppFields)
                .build().getMappingGroups();
    }

    @Override
    public List<AppSyncMode> getSyncModes(SendgridAppConfig config, SendgridAppSyncConfig mappingConfig) {
        return Lists.newArrayList(AppSyncMode.UPSERT);
    }

    @Override
    public Class<SendgridAppConfig> getAppConfigType() {
        return SendgridAppConfig.class;
    }
}
