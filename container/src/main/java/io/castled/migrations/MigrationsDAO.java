package io.castled.migrations;

import io.castled.constants.TableFields;
import io.castled.dtos.querymodel.ModelInputDTO;
import io.castled.migrations.models.MigrationDetails;
import io.castled.migrations.models.PipelineAndMapping;
import io.castled.models.Pipeline;
import io.castled.utils.JsonUtils;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RegisterRowMapper(MigrationsDAO.PipelineAndMappingRowMapper.class)
public interface MigrationsDAO {

    @SqlQuery("select id, mapping from pipelines where is_deleted =0")
    List<PipelineAndMapping> getOldMappings();

    @SqlUpdate("update pipelines set mapping =:mapping where id = :pipelineId")
    void updateMapping(@Bind("pipelineId") Long pipelineId, @Bind("mapping") String mappingStr);

    @SqlQuery("select * from pipelines where source_query is not null and model_id is null and is_deleted = 0 ")
    List<Pipeline> listPipelinesTobeMigrated();

    @GetGeneratedKeys
    @SqlUpdate("insert into migration_details(migration_type)" +
            " values(:migrationType)")
    long createMigrationDetails(@Bind("migrationType") MigrationType migrationType);

    @SqlUpdate("update migration_details set migration_status =:migrationStatus where migration_type = :migrationType")
    void updateMigrationStatus(@Bind("migrationType") MigrationType migrationType, @Bind("migrationStatus") MigrationStatus migrationStatus);

    @SqlQuery("select * from migration_details where migration_type = :migrationType and is_deleted = 0 ")
    MigrationDetails getMigrationDetails(@Bind("migrationType") MigrationType migrationType);



    class PipelineAndMappingRowMapper implements RowMapper<PipelineAndMapping> {

        @Override
        public PipelineAndMapping map(ResultSet rs, StatementContext ctx) throws SQLException {
            OldMappingConfig mapping = JsonUtils.jsonStringToObject(rs.getString("mapping"), OldMappingConfig.class);
            return new PipelineAndMapping(rs.getLong(TableFields.ID), mapping);
        }
    }

}
