package io.castled.migrations;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.castled.migrations.models.PipelineAndMapping;
import io.castled.models.DataMappingType;
import io.castled.models.TargetFieldsMapping;
import io.castled.utils.JsonUtils;
import org.jdbi.v3.core.Jdbi;


@Singleton
public class MigrationsService {

    private final MigrationsDAO migrationsDAO;

    @Inject
    public MigrationsService(Jdbi jdbi) {
        this.migrationsDAO = jdbi.onDemand(MigrationsDAO.class);
    }

    public void migrateDataMapping() {
        for (PipelineAndMapping pipelineAndMapping : migrationsDAO.getOldMappings()) {
            TargetFieldsMapping targetFieldsMapping = new TargetFieldsMapping();
            targetFieldsMapping.setFieldMappings(pipelineAndMapping.getMapping().getFieldMappings());
            targetFieldsMapping.setType(DataMappingType.TARGET_FIELDS_MAPPING);
            targetFieldsMapping.setPrimaryKeys(pipelineAndMapping.getMapping().getPrimaryKeys());
            this.migrationsDAO.updateMapping(pipelineAndMapping.getId(), JsonUtils.objectToString(targetFieldsMapping));
        }
    }
}
