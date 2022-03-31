package io.castled.apps.connectors.fbconversion;

import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.connectors.fbconversion.client.dtos.CustomDataField;
import io.castled.apps.connectors.fbconversion.client.dtos.CustomerInfoField;
import io.castled.apps.connectors.fbconversion.client.dtos.ServerEventField;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.mapping.FixedGroupAppField;
import io.castled.schema.mapping.MappingGroup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FbConversionAppConnector implements ExternalAppConnector<FbConversionAppConfig, FbConversionDataWriter, FbConversionSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(FbConversionAppConfig config, FbConversionSyncConfig mappingConfig) {
        return null;
    }

    @Override
    public FbConversionDataWriter getDataSink() {
        return ObjectRegistry.getInstance(FbConversionDataWriter.class);
    }

    @Override
    public ExternalAppSchema getSchema(FbConversionAppConfig config, FbConversionSyncConfig fbConversionSyncConfig) {
        return new ExternalAppSchema(FbConversionSchemaUtils.getSchema());
    }

    @Override
    public List<MappingGroup> getMappingGroups(FbConversionAppConfig config, FbConversionSyncConfig fbConversionSyncConfig) {

        List<FixedGroupAppField> serverGroupAppFields = Arrays.stream(ServerEventField.values())
                .map(field -> new FixedGroupAppField(field.getName(), field.getDisplayName(), field.isOptional()))
                .collect(Collectors.toList());

        List<FixedGroupAppField> customerGroupAppFields = Arrays.stream(CustomerInfoField.values())
                .map(field -> new FixedGroupAppField(field.getName(), field.getDisplayName(), true))
                .collect(Collectors.toList());

        List<FixedGroupAppField> customDataGroupAppFields = Arrays.stream(CustomDataField.values())
                .map(field -> new FixedGroupAppField(field.getName(), field.getDisplayName(), true))
                .collect(Collectors.toList());

        return MappingGroupAggregator.builder()
                .addFixedAppFields("Server Event Parameters",
                        "Please provide mapping for the server event fields",
                        serverGroupAppFields)
                .addFixedAppFields("Customer Information Parameters",
                        "You must map at least one of the following field for the event to be considered valid",
                        customerGroupAppFields)
                .addFixedAppFields("Custom Data Parameters",
                        "Please provide mapping for these parameters to send any additional data for ads delivery optimization",
                        customDataGroupAppFields)
                .addElasticAppFields("User Defined Custom Properties",
                        "You can provide your own custom properties if the the above Custom Data Parameters doesn't have the fields you are looking for",
                        false, false)
                .build().getMappingGroups();
    }

    @Override
    public Class<FbConversionSyncConfig> getMappingConfigType() {
        return FbConversionSyncConfig.class;
    }

    @Override
    public Class<FbConversionAppConfig> getAppConfigType() {
        return FbConversionAppConfig.class;
    }
}
