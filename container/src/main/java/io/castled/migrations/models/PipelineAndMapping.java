package io.castled.migrations.models;

import io.castled.migrations.OldMappingConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PipelineAndMapping {
    private Long id;
    private OldMappingConfig mapping;
}
