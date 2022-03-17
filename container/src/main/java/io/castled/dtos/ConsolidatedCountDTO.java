package io.castled.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsolidatedCountDTO {
    private int warehouses;
    private int apps;
    private int models;
    private int pipelines;
}
