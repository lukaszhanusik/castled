package io.castled.migrations;

import io.castled.apps.syncconfigs.AppSyncConfig;
import io.castled.constants.TableFields;
import io.castled.migrations.models.PipelineAndMapping;
import io.castled.models.*;
import io.castled.models.jobschedule.JobSchedule;
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
@RegisterRowMapper(MigrationsDAO.PipelineRowMapper.class)
public interface MigrationsDAO {

    @SqlQuery("select id, mapping from pipelines where is_deleted =0")
    List<PipelineAndMapping> getOldMappings();

    @SqlUpdate("update pipelines set mapping =:mapping where id = :pipelineId")
    void updateMapping(@Bind("pipelineId") Long pipelineId, @Bind("mapping") String mappingStr);

    @SqlQuery("select * from pipelines where source_query is not null and model_id is null and is_deleted = 0 ")
    List<Pipeline> listPipelinesTobeMigrated();

    @SqlQuery("select * from pipelines where source_query is not null and ( model_id is null or model_id = 0) and is_deleted = 0 ")
    List<Pipeline> fetchPipelinesWithoutModelId();

    @SqlUpdate("update pipelines set model_id =:modelId where id = :id")
    void updateModelIdForPipeline(@Bind("id") Long id, @Bind("modelId") Long modelId);

    class PipelineAndMappingRowMapper implements RowMapper<PipelineAndMapping> {

        @Override
        public PipelineAndMapping map(ResultSet rs, StatementContext ctx) throws SQLException {
            OldMappingConfig mapping = JsonUtils.jsonStringToObject(rs.getString("mapping"), OldMappingConfig.class);
            return new PipelineAndMapping(rs.getLong(TableFields.ID), mapping);
        }
    }

    class PipelineRowMapper implements RowMapper<Pipeline> {

        @Override
        public Pipeline map(ResultSet rs, StatementContext ctx) throws SQLException {
            AppSyncConfig appSyncConfig = JsonUtils.jsonStringToObject(rs.getString("app_sync_config"), AppSyncConfig.class);
            CastledDataMapping mapping = JsonUtils.jsonStringToObject(rs.getString("mapping"), CastledDataMapping.class);
            JobSchedule jobSchedule = JsonUtils.jsonStringToObject(rs.getString(TableFields.SCHEDULE), JobSchedule.class);

            return Pipeline.builder().id(rs.getLong(TableFields.ID)).name(rs.getString(TableFields.NAME))
                    .status(PipelineStatus.valueOf(rs.getString(TableFields.STATUS)))
                    .seqId(rs.getLong(TableFields.SEQ_ID)).appSyncConfig(appSyncConfig)
                    .dataMapping(mapping).uuid(rs.getString(TableFields.UUID)).isDeleted(rs.getBoolean(TableFields.ID))
                    .jobSchedule(jobSchedule).modelId(rs.getLong("model_id")).sourceQuery(rs.getString("source_query"))
                    .teamId(rs.getLong(TableFields.TEAM_ID)).queryMode(QueryMode.valueOf(rs.getString("query_mode")))
                    .appId(rs.getLong(TableFields.APP_ID)).warehouseId(rs.getLong(TableFields.WAREHOUSE_ID))
                    .syncStatus(PipelineSyncStatus.valueOf(rs.getString("sync_status"))).build();
        }
    }

}
