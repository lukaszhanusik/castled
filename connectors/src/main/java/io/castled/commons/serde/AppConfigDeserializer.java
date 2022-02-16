package io.castled.commons.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.castled.ObjectRegistry;
import io.castled.apps.AppConfig;
import io.castled.apps.ExternalAppType;
import io.castled.commons.factories.ConnectorFactory;
import io.castled.utils.JsonUtils;


public class AppConfigDeserializer extends StdDeserializer<AppConfig> {

    protected AppConfigDeserializer() {
        super(AppConfig.class);
    }

    @Override
    public AppConfig deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        JsonNode jsonNode = JsonUtils.jsonParserToJsonNode(jsonParser);
        ExternalAppType externalAppType = ExternalAppType.valueOf(jsonNode.get("type").asText());
        return ObjectRegistry.getInstance(ConnectorFactory.class)
                .getAppConnector(externalAppType).getAppConfig(jsonNode);
    }
}
