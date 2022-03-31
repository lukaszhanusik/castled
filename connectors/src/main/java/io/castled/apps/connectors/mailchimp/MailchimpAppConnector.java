package io.castled.apps.connectors.mailchimp;

import com.google.common.collect.Lists;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.OAuthAppConfig;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.connectors.mailchimp.client.MailchimpRestClient;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.mapping.FixedGroupAppField;
import io.castled.mapping.PrimaryKeyGroupField;
import io.castled.schema.mapping.MappingGroup;
import io.castled.schema.models.RecordSchema;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MailchimpAppConnector implements ExternalAppConnector<OAuthAppConfig,
        MailchimpDataWriter, MailchimpAppSyncConfig> {


    @Override
    public List<FormFieldOption> getAllObjects(OAuthAppConfig config, MailchimpAppSyncConfig mailchimpAppSyncConfig) {
        MailchimpRestClient mailchimpRestClient = new MailchimpRestClient(config.getOAuthToken());
        return mailchimpRestClient.getAllAudiences().stream()
                .map(audience -> new FormFieldOption(new MailchimpAudienceSyncObject(audience.getId(), audience.getName()), audience.getName())).collect(Collectors.toList());
    }

    @Override
    public MailchimpDataWriter getDataSink() {
        return ObjectRegistry.getInstance(MailchimpDataWriter.class);
    }

    @Override
    public ExternalAppSchema getSchema(OAuthAppConfig config, MailchimpAppSyncConfig mailchimpAppSyncConfig) {
        RecordSchema.Builder customerSchemaBuilder = RecordSchema.builder();
        for (MailchimpObjectFields.AUDIENCE_FIELDS field : MailchimpObjectFields.AUDIENCE_FIELDS.values()) {
            customerSchemaBuilder.put(field.getFieldName(), field.getSchema());
        }
        return new ExternalAppSchema(customerSchemaBuilder.build());
    }

    public List<AppSyncMode> getSyncModes(OAuthAppConfig config, MailchimpAppSyncConfig mailchimpAppSyncConfig) {
        return Lists.newArrayList(AppSyncMode.UPSERT);
    }

    public Class<MailchimpAppSyncConfig> getMappingConfigType() {
        return MailchimpAppSyncConfig.class;
    }

    @Override
    public Class<OAuthAppConfig> getAppConfigType() {
        return OAuthAppConfig.class;
    }

    public List<MappingGroup> getMappingGroups(OAuthAppConfig oAuthAppConfig, MailchimpAppSyncConfig mailchimpAppSyncConfig) {
        List<PrimaryKeyGroupField> primaryKeyGroupFields = Lists.newArrayList(new PrimaryKeyGroupField(MailchimpObjectFields.AUDIENCE_FIELDS.EMAIL.getFieldName(),
                MailchimpObjectFields.AUDIENCE_FIELDS.EMAIL.getFieldName(), false));
        List<FixedGroupAppField> fixedGroupAppFields = Arrays.stream(MailchimpObjectFields.AUDIENCE_FIELDS.values()).map(MailchimpObjectFields.AUDIENCE_FIELDS::getFieldName)
                .map(field -> new FixedGroupAppField(field, field, true)).collect(Collectors.toList());
        return MappingGroupAggregator.builder().addPrimaryKeyFields(primaryKeyGroupFields).addFixedAppFields(fixedGroupAppFields).build().getMappingGroups();
    }
}
