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
public class DataMigration {
    private Long id;
    private MigrationType type;
    private MigrationStatus status;
    private String failureMessage;

    public boolean isPending() {
        return status != MigrationStatus.PROCESSED;
    }

    public boolean isCompleted() {
        return status == MigrationStatus.PROCESSED;
    }
}
