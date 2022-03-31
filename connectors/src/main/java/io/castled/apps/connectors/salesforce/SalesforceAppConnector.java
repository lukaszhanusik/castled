package io.castled.apps.connectors.salesforce;

import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.ExternalAppType;
import io.castled.apps.OAuthAppConfig;
import io.castled.apps.connectors.salesforce.client.SFDCRestClient;
import io.castled.apps.connectors.salesforce.client.SalesforceHelper;
import io.castled.apps.connectors.salesforce.client.dtos.SalesforceField;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.GenericSyncObject;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.mapping.FixedGroupAppField;
import io.castled.mapping.PrimaryKeyGroupField;
import io.castled.schema.mapping.MappingGroup;

import java.util.List;
import java.util.stream.Collectors;

public class SalesforceAppConnector implements ExternalAppConnector<OAuthAppConfig, SalesforceDataWriter,
        SalesforceAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(OAuthAppConfig config, SalesforceAppSyncConfig mappingConfig) {
        SFDCRestClient sfdcRestClient = new SFDCRestClient(config.getOAuthToken(),
                config.getClientConfig());
        return sfdcRestClient.getAllObjects().stream().map(sfdcObject -> new FormFieldOption
                (new GenericSyncObject(sfdcObject.getName(), ExternalAppType.SALESFORCE), sfdcObject.getName())).collect(Collectors.toList());
    }

    @Override
    public SalesforceDataWriter getDataSink() {
        return ObjectRegistry.getInstance(SalesforceDataWriter.class);
    }

    @Override
    public ExternalAppSchema getSchema(OAuthAppConfig salesforceAppConfig, SalesforceAppSyncConfig mappingConfig) {
        SFDCRestClient sfdcRestClient = new SFDCRestClient(salesforceAppConfig.getOAuthToken(),
                salesforceAppConfig.getClientConfig());
        List<SalesforceField> fields = sfdcRestClient.getObjectDetails(mappingConfig.getObject().getObjectName()).getFields();
        return new ExternalAppSchema(SalesforceHelper.getRecordSchema(mappingConfig.getObject().getObjectName(), fields));
    }

    @Override
    public Class<SalesforceAppSyncConfig> getMappingConfigType() {
        return SalesforceAppSyncConfig.class;
    }

    @Override
    public Class<OAuthAppConfig> getAppConfigType() {
        return OAuthAppConfig.class;
    }

    public List<MappingGroup> getMappingGroups(OAuthAppConfig oAuthAppConfig, SalesforceAppSyncConfig hubspotAppSyncConfig) {
        SFDCRestClient sfdcRestClient = new SFDCRestClient(oAuthAppConfig.getOAuthToken(),
                oAuthAppConfig.getClientConfig());
        List<SalesforceField> fields = sfdcRestClient.getObjectDetails(hubspotAppSyncConfig.getObject().getObjectName()).getFields();
        List<PrimaryKeyGroupField> primaryKeyGroupFields = fields.stream().filter(SalesforceHelper::checkPkEligibility).map(SalesforceField::getName)
                .map(field -> new PrimaryKeyGroupField(field, field, true)).collect(Collectors.toList());

        List<FixedGroupAppField> fixedGroupAppFields = fields.stream().map(SalesforceField::getName)
                .map(field -> new FixedGroupAppField(field, field, true)).collect(Collectors.toList());
        return MappingGroupAggregator.builder().addPrimaryKeyFields(primaryKeyGroupFields)
                .addFixedAppFields(fixedGroupAppFields).build().getMappingGroups();
    }
}
