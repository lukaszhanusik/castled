package io.castled.warehouses;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.castled.commons.serde.WarehouseConfigDeserializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonDeserialize(using = WarehouseConfigDeserializer.class)
public abstract class WarehouseConfig {
    private WarehouseType type;
}
