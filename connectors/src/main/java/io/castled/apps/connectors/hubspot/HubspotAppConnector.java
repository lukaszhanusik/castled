package io.castled.apps.connectors.hubspot;

import com.google.common.collect.Lists;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.OAuthAppConfig;
import io.castled.apps.connectors.hubspot.client.HubspotRestClient;
import io.castled.apps.connectors.hubspot.client.dtos.HubspotProperty;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.mapping.FixedGroupAppField;
import io.castled.mapping.PrimaryKeyGroupField;
import io.castled.schema.mapping.MappingGroup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HubspotAppConnector implements ExternalAppConnector<OAuthAppConfig, HubspotDataWriter,
        HubspotAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(OAuthAppConfig config, HubspotAppSyncConfig hubspotAppSyncConfig) {

        HubspotRestClient hubspotRestClient = new HubspotRestClient(config.getOAuthToken(),
                config.getClientConfig());
        List<FormFieldOption> fieldOptions = hubspotRestClient.getHubspotSchemas()
                .stream().map(hubspotSchema -> new FormFieldOption(new HubspotSyncObject(hubspotSchema.getName(), hubspotSchema.getObjectTypeId()), hubspotSchema.getName())).collect(Collectors.toList());
        fieldOptions.addAll(Arrays.stream(HubspotStandardObject.values()).map(hsStandardObject -> new FormFieldOption(new HubspotSyncObject(hsStandardObject.name(),
                hsStandardObject.name()), hsStandardObject.name())).collect(Collectors.toList()));
        return fieldOptions;
    }


    @Override
    public ExternalAppSchema getSchema(OAuthAppConfig config, HubspotAppSyncConfig mappingConfig) {
        HubspotRestClient hubspotRestClient = new HubspotRestClient(config.getOAuthToken(),
                config.getClientConfig());
        List<HubspotProperty> hubspotProperties = hubspotRestClient.getObjectProperties(mappingConfig.getObject().getTypeId())
                .stream().filter(hubspotProperty -> !hubspotProperty.getModificationMetadata().isReadOnlyValue()).collect(Collectors.toList());

        return new ExternalAppSchema(HubspotUtils.getSchema(mappingConfig.getObject().getName(), hubspotProperties));
    }

    @Override
    public Class<HubspotAppSyncConfig> getMappingConfigType() {
        return HubspotAppSyncConfig.class;
    }

    @Override
    public Class<OAuthAppConfig> getAppConfigType() {
        return OAuthAppConfig.class;
    }

    @Override
    public HubspotDataWriter getDataSink() {
        return ObjectRegistry.getInstance(HubspotDataWriter.class);
    }

    public List<MappingGroup> getMappingGroups(OAuthAppConfig oAuthAppConfig, HubspotAppSyncConfig hubspotAppSyncConfig) {

        HubspotRestClient hubspotRestClient = new HubspotRestClient(oAuthAppConfig.getOAuthToken(), oAuthAppConfig.getClientConfig());

        List<HubspotProperty> hubspotProperties = hubspotRestClient.getObjectProperties(hubspotAppSyncConfig.getObject().getTypeId())
                .stream().filter(hubspotProperty -> !hubspotProperty.getModificationMetadata().isReadOnlyValue()).collect(Collectors.toList());
        return MappingGroupAggregator.builder().addPrimaryKeyFields(getPrimaryKeyFields(hubspotProperties, hubspotAppSyncConfig.getObject().getName()))
                .addFixedAppFields(getFixedAppFields(hubspotProperties, hubspotAppSyncConfig.getObject().getName())).build().getMappingGroups();
    }

    private List<PrimaryKeyGroupField> getPrimaryKeyFields(List<HubspotProperty> hubspotProperties, String objectName) {
        if (objectName.equals(HubspotStandardObject.contacts.name())) {
            return Lists.newArrayList(new PrimaryKeyGroupField(HubspotFields.EMAIL_FIELD, HubspotFields.EMAIL_FIELD, false));
        }
        if (objectName.equals(HubspotStandardObject.companies.name())) {
            return Lists.newArrayList(new PrimaryKeyGroupField(HubspotFields.COMPANY_DOMAIN, HubspotFields.COMPANY_DOMAIN, false));
        }
        return hubspotProperties.stream().map(HubspotProperty::getName).map(hubspotProperty ->
                new PrimaryKeyGroupField(hubspotProperty, hubspotProperty, true)).collect(Collectors.toList());

    }

    private List<FixedGroupAppField> getFixedAppFields(List<HubspotProperty> hubspotProperties, String objectName) {
        List<FixedGroupAppField> fixedGroupAppFields = hubspotProperties.stream().map(HubspotProperty::getName).map(hubspotProperty ->
                new FixedGroupAppField(hubspotProperty, hubspotProperty, true)).collect(Collectors.toList());
        if (objectName.equals(HubspotStandardObject.contacts.name())) {
            return fixedGroupAppFields.stream().filter(fixedGroupAppField -> !fixedGroupAppField.getName().equals(HubspotFields.EMAIL_FIELD))
                    .collect(Collectors.toList());
        }
        if (objectName.equals(HubspotStandardObject.companies.name())) {
            return fixedGroupAppFields.stream().filter(fixedGroupAppField -> !fixedGroupAppField.getName().equals(HubspotFields.COMPANY_DOMAIN))
                    .collect(Collectors.toList());
        }
        return fixedGroupAppFields;
    }
}
