package io.castled.apps.connectors.hubspot;

import com.google.common.collect.Lists;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.OAuthAppConfig;
import io.castled.apps.connectors.hubspot.client.HubspotRestClient;
import io.castled.apps.connectors.hubspot.client.dtos.HubspotProperty;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.forms.dtos.FormFieldOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HubspotAppConnector implements ExternalAppConnector<OAuthAppConfig, HubspotDataSink,
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

        return new ExternalAppSchema(HubspotUtils.getSchema(mappingConfig.getObject().getName(), hubspotProperties),
                getPrimaryKeyEligibles(hubspotProperties, mappingConfig.getObject().getName()));
    }

    private List<String> getPrimaryKeyEligibles(List<HubspotProperty> hubspotProperties, String objectName) {

        if (objectName.equals(HubspotStandardObject.contacts.name())) {
            return Lists.newArrayList(HubspotFields.EMAIL_FIELD);
        }
        if (objectName.equals(HubspotStandardObject.companies.name())) {
            return Lists.newArrayList(HubspotFields.COMPANY_DOMAIN);
        }
        return hubspotProperties.stream().map(HubspotProperty::getName).collect(Collectors.toList());
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
    public HubspotDataSink getDataSink() {
        return ObjectRegistry.getInstance(HubspotDataSink.class);
    }
}
