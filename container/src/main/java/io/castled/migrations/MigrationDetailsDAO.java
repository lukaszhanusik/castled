package io.castled.migrations;

import io.castled.constants.TableFields;
import io.castled.migrations.models.MigrationDetails;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;

@RegisterRowMapper(MigrationDetailsDAO.MigrationDetailsRowMapper.class)
public interface MigrationDetailsDAO {

    @GetGeneratedKeys
    @SqlUpdate("insert into migration_details(migration_type)" +
            " values(:migrationType)")
    long createMigrationDetails(@Bind("migrationType") MigrationType migrationType);

    @SqlUpdate("update migration_details set migration_status =:migrationStatus , details =:details where migration_type = :migrationType")
    void updateMigrationStatus(@Bind("migrationType") MigrationType migrationType, @Bind("migrationStatus") MigrationStatus migrationStatus,
                               @Bind("details") String details);

    @SqlQuery("select * from migration_details where migration_type = :migrationType and is_deleted = 0 ")
    MigrationDetails getMigrationDetails(@Bind("migrationType") MigrationType migrationType);

    class MigrationDetailsRowMapper implements RowMapper<MigrationDetails> {

        @Override
        public MigrationDetails map(ResultSet rs, StatementContext ctx) throws SQLException {
            return MigrationDetails.builder().id(rs.getLong(TableFields.ID))
                    .migrationType(MigrationType.valueOf(rs.getString("migration_type")))
                    .status(MigrationStatus.valueOf(rs.getString("migration_status")))
                    .details(rs.getString("details"))
                    .build();
        }
    }

}
