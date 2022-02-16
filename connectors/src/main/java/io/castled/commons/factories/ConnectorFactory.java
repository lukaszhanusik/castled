package io.castled.commons.factories;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.ExternalAppType;
import io.castled.warehouses.WarehouseConnector;
import io.castled.warehouses.WarehouseType;

import java.util.Map;

@SuppressWarnings("rawtypes")
@Singleton
public class ConnectorFactory {

    private final Map<WarehouseType, WarehouseConnector> warehouseConnectors;
    private final Map<ExternalAppType, ExternalAppConnector> appConnectors;

    @Inject
    public ConnectorFactory(Map<WarehouseType, WarehouseConnector> warehouseConnectors,
                            Map<ExternalAppType, ExternalAppConnector> appConnectors) {
        this.warehouseConnectors = warehouseConnectors;
        this.appConnectors = appConnectors;
    }

    public WarehouseConnector getWarehouseConnector(WarehouseType warehouseType) {
        return warehouseConnectors.get(warehouseType);
    }

    public ExternalAppConnector getAppConnector(ExternalAppType externalAppType) {
        return appConnectors.get(externalAppType);
    }
}
