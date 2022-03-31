package io.castled.apps.connectors.marketo;

import com.google.common.collect.Lists;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.ExternalAppType;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.GenericSyncObject;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.mapping.FixedGroupAppField;
import io.castled.mapping.PrimaryKeyGroupField;
import io.castled.schema.mapping.MappingGroup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MarketoAppConnector implements ExternalAppConnector<MarketoAppConfig, MarketoDataWriter,
        MarketoAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(MarketoAppConfig config, MarketoAppSyncConfig mappingConfig) {
        return Arrays.stream(MarketoObject.values())
                .map(marketoObject -> new FormFieldOption(new GenericSyncObject(marketoObject.getName(),
                        ExternalAppType.MARKETO), marketoObject.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public MarketoDataWriter getDataSink() {
        return ObjectRegistry.getInstance(MarketoDataWriter.class);
    }

    @Override
    public ExternalAppSchema getSchema(MarketoAppConfig config, MarketoAppSyncConfig marketoAppSyncConfig) {
        MarketoBulkClient marketoBulkClient = new MarketoBulkClient(config);
        MarketoObject marketoObject = MarketoObject.getObjectByName(marketoAppSyncConfig.getObject().getObjectName());
        ObjectAttributesContainer attrsContainer = marketoBulkClient.getAttributes(marketoObject);
        return new ExternalAppSchema(
                MarketoUtils.getSchema(marketoObject, attrsContainer.getAttributes(marketoAppSyncConfig.getMode()),
                        attrsContainer.getDedupeAttrFieldMap()));
    }

    @Override
    public Class<MarketoAppSyncConfig> getMappingConfigType() {
        return MarketoAppSyncConfig.class;
    }

    @Override
    public List<AppSyncMode> getSyncModes(MarketoAppConfig config, MarketoAppSyncConfig mappingConfig) {
        return Lists.newArrayList(AppSyncMode.UPSERT, AppSyncMode.UPDATE);
    }

    @Override
    public List<MappingGroup> getMappingGroups(MarketoAppConfig config, MarketoAppSyncConfig marketoAppSyncConfig) {
        MarketoBulkClient marketoBulkClient = new MarketoBulkClient(config);
        MarketoObject marketoObject = MarketoObject.getObjectByName(marketoAppSyncConfig.getObject().getObjectName());
        ObjectAttributesContainer attrsContainer = marketoBulkClient.getAttributes(marketoObject);

        // Primary key group
        List<PrimaryKeyGroupField> primaryKeyGroupFields =
                attrsContainer.getPkEligibles(marketoAppSyncConfig.getMode()).stream()
                        .map(pk -> new PrimaryKeyGroupField(pk, pk, true)).collect(Collectors.toList());

        // Fixed field group
        List<FixedGroupAppField> fixedGroupAppFields =
                attrsContainer.getAttributes(marketoAppSyncConfig.getMode()).stream()
                .map(attr -> new FixedGroupAppField(attr.getName(), attr.getDisplayName(), true))
                .collect(Collectors.toList());

        return MappingGroupAggregator.builder()
                .addPrimaryKeyFields(primaryKeyGroupFields)
                .addFixedAppFields(fixedGroupAppFields)
                .build().getMappingGroups();
    }

    @Override
    public Class<MarketoAppConfig> getAppConfigType() {
        return MarketoAppConfig.class;
    }

}
