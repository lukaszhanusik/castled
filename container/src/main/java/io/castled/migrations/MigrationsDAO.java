package io.castled.migrations;

import io.castled.constants.TableFields;
import io.castled.migrations.models.PipelineAndMapping;
import io.castled.utils.JsonUtils;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
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


    class PipelineAndMappingRowMapper implements RowMapper<PipelineAndMapping> {

        @Override
        public PipelineAndMapping map(ResultSet rs, StatementContext ctx) throws SQLException {
            OldMappingConfig mapping = JsonUtils.jsonStringToObject(rs.getString("mapping"), OldMappingConfig.class);
            return new PipelineAndMapping(rs.getLong(TableFields.ID), mapping);
        }
    }

}
