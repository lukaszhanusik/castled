package io.castled.commons.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppType;
import io.castled.apps.syncconfigs.AppSyncConfig;
import io.castled.commons.factories.ConnectorFactory;
import io.castled.utils.JsonUtils;

public class AppSyncConfigDeserializer extends StdDeserializer<AppSyncConfig> {

    protected AppSyncConfigDeserializer() {
        super(AppSyncConfig.class);
    }

    @Override
    public AppSyncConfig deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        JsonNode jsonNode = JsonUtils.jsonParserToJsonNode(jsonParser);
        ExternalAppType externalAppType = ExternalAppType.valueOf(jsonNode.get("appType").asText());
        return ObjectRegistry.getInstance(ConnectorFactory.class)
                .getAppConnector(externalAppType).getAppSyncConfig(jsonNode);
    }
}
