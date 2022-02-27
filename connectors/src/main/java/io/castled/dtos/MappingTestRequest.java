package io.castled.dtos;

import io.castled.apps.syncconfigs.AppSyncConfig;
import io.castled.models.CastledDataMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MappingTestRequest {

    private List<String> queryFields;
    private CastledDataMapping mapping;
    private AppSyncConfig appSyncConfig;
}
