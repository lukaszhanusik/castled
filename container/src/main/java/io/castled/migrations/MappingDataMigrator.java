package io.castled.migrations;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Sets;
import com.google.common.collect.Maps;
import io.castled.ObjectRegistry;
import io.castled.daos.PipelineDAO;
import io.castled.dtos.querymodel.ModelInputDTO;
import io.castled.dtos.querymodel.SqlQueryModelDetails;
import io.castled.models.Pipeline;
import io.castled.models.QueryModelPK;
import io.castled.models.TargetFieldsMapping;
import io.castled.models.Warehouse;
import io.castled.services.PipelineService;
import io.castled.services.QueryModelService;
import io.castled.warehouses.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class MappingDataMigrator extends AbstractDataMigrator {
    @Override
    public MigrationType getMigrationType() {
        return MigrationType.MAPPING_MIGRATION;
    }

    @Override
    public void migrateData() {

        MigrationsDAO pipelineDAO = ObjectRegistry.getInstance(Jdbi.class).onDemand(MigrationsDAO.class);
        PipelineService pipelineService = ObjectRegistry.getInstance(PipelineService.class);
        WarehouseService warehouseService = ObjectRegistry.getInstance(WarehouseService.class);
        QueryModelService queryModelService = ObjectRegistry.getInstance(QueryModelService.class);
        List<Pipeline> pipelineList = pipelineDAO.fetchPipelinesWithoutModelId();

        Map<Long, Set<Long>> warehousePipelineMap = Maps.newHashMap();
        Map<Long, Pipeline> pipelineMap = Maps.newHashMap();
        Map<Long, Map<String, Long>> handledQueryMap = Maps.newHashMap();

        pipelineList.stream().forEach(pipeline -> {
            pipelineMap.put(pipeline.getId(), pipeline);
            if (!warehousePipelineMap.containsKey(pipeline.getWarehouseId())) {
                warehousePipelineMap.put(pipeline.getWarehouseId(), Sets.newHashSet());
            }
            warehousePipelineMap.get(pipeline.getWarehouseId()).add(pipeline.getId());
        });

        warehousePipelineMap.forEach((warehouseId, pipelines) -> {
            Warehouse warehouse = warehouseService.getWarehouse(warehouseId);
            pipelines.forEach(pipelineId -> {
                Long modelId = null;

                Pipeline pipeline = pipelineMap.get(pipelineId);
                String sourceQuery = pipeline.getSourceQuery();
                if (!handledQueryMap.containsKey(warehouseId)) {
                    handledQueryMap.put(warehouseId, Maps.newHashMap());
                }

                if (handledQueryMap.containsKey(warehouseId) && handledQueryMap.get(warehouseId).containsKey(sourceQuery)
                        && handledQueryMap.get(warehouseId).get(sourceQuery) != null) {
                    //model already created, can you the same model iD
                    modelId = handledQueryMap.get(warehouseId).get(sourceQuery);
                }

                if (modelId == null) {
                    List<String> oldAppPKs = pipeline.getDataMapping().getPrimaryKeys();
                    Set<String> newWarehousePKs = Sets.newHashSet();

                    if (pipeline.getDataMapping() instanceof TargetFieldsMapping) {
                        TargetFieldsMapping targetFieldsMapping = (TargetFieldsMapping) pipeline.getDataMapping();
                        targetFieldsMapping.getFieldMappings().forEach(fieldMapping -> {
                            if (oldAppPKs.contains(fieldMapping.getAppField())) {
                                newWarehousePKs.add(fieldMapping.getWarehouseField());
                            }
                        });
                    }

                    ModelInputDTO modelInputDTO = new ModelInputDTO();
                    modelInputDTO.setWarehouseId(warehouseId);
                    modelInputDTO.setModelName("Model-" + pipeline.getName());
                    modelInputDTO.setModelType("SQL");
                    modelInputDTO.setDemo(warehouse.isDemo());
                    SqlQueryModelDetails sqlQueryModelDetails = new SqlQueryModelDetails();
                    sqlQueryModelDetails.setSourceQuery(sourceQuery);
                    modelInputDTO.setModelDetails(sqlQueryModelDetails);
                    QueryModelPK queryModelPK = new QueryModelPK();
                    queryModelPK.setPrimaryKeys(Lists.newArrayList(newWarehousePKs));
                    modelInputDTO.setQueryModelPK(queryModelPK);
                    modelId = queryModelService.createQueryModel(modelInputDTO, pipeline.getTeamId());
                    handledQueryMap.get(warehouseId).put(sourceQuery, modelId);
                }
                //update modelId in pipeline
                if (modelId != null) {
                    pipelineDAO.updateModelIdForPipeline(pipelineId, modelId);
                }
            });
        });
    }
}
