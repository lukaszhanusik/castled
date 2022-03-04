package io.castled.migrations;

import io.castled.constants.TableFields;
import io.castled.migrations.models.DataMigration;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;

@RegisterRowMapper(DataMigrationsDAO.MigrationDetailsRowMapper.class)
public interface DataMigrationsDAO {

    @SqlUpdate("insert into data_migrations(type, status) values(:type, 'CREATED')")
    void createDataMigration(@Bind("type") MigrationType migrationType);

    @SqlUpdate("update data_migrations set status ='PROCESSED' where type = :type")
    void markMigrationProcessed(@Bind("type") MigrationType migrationType);

    @SqlUpdate("update data_migrations set status ='FAILED', failure_message = :failureMessage where type = :type")
    void markMigrationFailed(@Bind("type") MigrationType migrationType, @Bind("failureMessage") String failureMessage);

    @SqlQuery("select * from data_migrations where type = :type and is_deleted = 0 ")
    DataMigration getDataMigration(@Bind("type") MigrationType migrationType);

    class MigrationDetailsRowMapper implements RowMapper<DataMigration> {

        @Override
        public DataMigration map(ResultSet rs, StatementContext ctx) throws SQLException {
            return DataMigration.builder().id(rs.getLong(TableFields.ID))
                    .type(MigrationType.valueOf(rs.getString(TableFields.TYPE)))
                    .status(MigrationStatus.valueOf(rs.getString(TableFields.STATUS)))
                    .failureMessage(rs.getString(TableFields.FAILURE_MESSAGE))
                    .build();
        }
    }
}
