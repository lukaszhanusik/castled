package io.castled.apps.connectors.restapi;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.commons.models.AppSyncMode;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.schema.mapping.MappingGroup;

import java.util.List;

@Singleton
public class RestApiAppConnector implements ExternalAppConnector<RestApiAppConfig,
        RestApiDataWriter, RestApiAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(RestApiAppConfig restApiAppConfig, RestApiAppSyncConfig restApiAppSyncConfig) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RestApiDataWriter getDataSink() {
        return ObjectRegistry.getInstance(RestApiDataWriter.class);
    }

    @Override
    public ExternalAppSchema getSchema(RestApiAppConfig restApiAppConfig, RestApiAppSyncConfig restApiAppSyncConfig) {
        return new ExternalAppSchema(null);
    }

    public List<AppSyncMode> getSyncModes(RestApiAppConfig restApiAppConfig, RestApiAppSyncConfig restApiAppSyncConfig) {
        return Lists.newArrayList(AppSyncMode.INSERT);
    }

    @Override
    public List<MappingGroup> getMappingGroups(RestApiAppConfig config, RestApiAppSyncConfig restApiAppSyncConfig) {
        return Lists.newArrayList();
    }

    public Class<RestApiAppSyncConfig> getMappingConfigType() {
        return RestApiAppSyncConfig.class;
    }

    @Override
    public Class<RestApiAppConfig> getAppConfigType() {
        return RestApiAppConfig.class;
    }
}
