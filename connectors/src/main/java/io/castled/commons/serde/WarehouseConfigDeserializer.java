package io.castled.commons.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import io.castled.ObjectRegistry;
import io.castled.commons.factories.ConnectorFactory;
import io.castled.utils.JsonUtils;
import io.castled.warehouses.WarehouseConfig;
import io.castled.warehouses.WarehouseType;

import java.io.IOException;

public class WarehouseConfigDeserializer extends StdDeserializer<WarehouseConfig> {

    protected WarehouseConfigDeserializer() {
        super(WarehouseConfig.class);
    }

    @Override
    public WarehouseConfig deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode jsonNode = JsonUtils.jsonParserToJsonNode(jsonParser);
        WarehouseType warehouseType = WarehouseType.valueOf(jsonNode.get("type").asText());
        return ObjectRegistry.getInstance(ConnectorFactory.class)
                .getWarehouseConnector(warehouseType).getConfig(jsonNode);
    }
}
