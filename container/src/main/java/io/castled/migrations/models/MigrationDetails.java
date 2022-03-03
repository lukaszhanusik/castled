package io.castled.migrations.models;

import io.castled.migrations.MigrationStatus;
import io.castled.migrations.MigrationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MigrationDetails {
    private Long id;
    private MigrationType migrationType;
    private MigrationStatus status;
    private String details;
}
