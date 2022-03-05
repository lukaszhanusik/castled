package io.castled.dtos.querymodel;

import io.castled.dtos.PipelineDTO;
import io.castled.dtos.WarehouseDetails;
import io.castled.models.QueryModelPK;
import io.castled.models.QueryModelType;
import io.castled.schema.models.FieldSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryModelDTO {
    private Long id;
    private Long userId;
    private Long teamId;
    private WarehouseDetails warehouse;
    private List<FieldSchema> columnDetails;
    private String name;
    private QueryModelType type;
    private QueryModelDetails details;
    private QueryModelPK queryPK;
    private List<PipelineDTO> activeSyncDetails;
    private Integer activeSyncsCount;
    private boolean demo;
}
