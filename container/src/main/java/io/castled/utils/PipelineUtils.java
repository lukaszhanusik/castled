package io.castled.utils;

import com.google.common.collect.Lists;
import io.castled.models.DataMappingType;
import io.castled.models.Pipeline;
import io.castled.models.TargetFieldsMapping;

import java.util.List;
import java.util.Map;

public class PipelineUtils {

    public static List<String> getWarehousePrimaryKeys(Pipeline pipeline) {
        if (pipeline.getDataMapping().getType() == DataMappingType.TARGET_REST_MAPPING) {
            return pipeline.getDataMapping().getPrimaryKeys();
        }
        return getWarehouseFields((TargetFieldsMapping) pipeline.getDataMapping(), pipeline.getDataMapping().getPrimaryKeys());
    }

    public static List<String> getWarehouseFields(TargetFieldsMapping dataMapping, List<String> appFields) {
        Map<String, String> appFieldsMapping = DataMappingUtils.getMappingForAppFields(dataMapping, appFields);
        if (appFieldsMapping == null) {
            return null;
        }
        return Lists.newArrayList(appFieldsMapping.values());
    }
}
