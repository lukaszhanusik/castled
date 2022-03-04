package io.castled.caches;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.castled.ObjectRegistry;
import io.castled.cache.CastledCache;

import io.castled.exceptions.CastledRuntimeException;
import io.castled.models.QueryModel;
import io.castled.models.Warehouse;
import io.castled.schema.models.RecordSchema;
import io.castled.services.QueryModelService;
import io.castled.utils.TimeUtils;
import io.castled.warehouses.WarehouseConnector;
import io.castled.warehouses.WarehouseService;
import io.castled.warehouses.WarehouseType;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@Singleton
@SuppressWarnings({"unchecked","rawtypes"})
@Slf4j
public class ModelSchemaCache extends CastledCache<Long, RecordSchema> {

    @Inject
    public ModelSchemaCache(Map<WarehouseType, WarehouseConnector> warehouseConnectors,
                            WarehouseService warehouseService) {
        super(TimeUtils.hoursToMillis(3), 1000,
                (modelId) -> getRecordSchema(modelId, warehouseService, warehouseConnectors), false);
    }

    private static RecordSchema getRecordSchema(Long modelId, WarehouseService warehouseService,
                                                Map<WarehouseType, WarehouseConnector> warehouseConnectors) {
        try {
            QueryModelService queryModelService = ObjectRegistry.getInstance(QueryModelService.class);
            QueryModel queryModel = queryModelService.getQueryModel(modelId);
            String sourceQuery = queryModelService.getSourceQuery(queryModel);
            Warehouse warehouse = warehouseService.getWarehouse(queryModel.getWarehouseId(), true);
            return warehouseConnectors.get(warehouse.getType()).getQuerySchema(warehouse.getConfig(), sourceQuery);
        } catch (Exception e) {
            log.error("Refresh record schema cache failed", e);
            return null;
        }
    }
}
