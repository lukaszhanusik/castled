package io.castled.daos;

import io.castled.constants.TableFields;
import io.castled.dtos.querymodel.ModelInputDTO;
import io.castled.dtos.querymodel.QueryModelDetails;
import io.castled.models.QueryModel;
import io.castled.models.QueryModelPK;
import io.castled.models.QueryModelType;
import io.castled.models.users.User;
import io.castled.utils.JsonUtils;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterArgumentFactory;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@RegisterRowMapper(QueryModelDAO.QueryModelRowMapper.class)
@RegisterArgumentFactory(QueryModelDAO.QueryModelArgumentFactory.class)
@RegisterArgumentFactory(QueryModelDAO.QueryModelPKArgumentFactory.class)
public interface QueryModelDAO {

    @GetGeneratedKeys
    @SqlUpdate("insert into query_models(user_id, team_id, warehouse_id, name, type, details, query_pk, demo)" +
            " values(:user.id, :user.teamId, :modelDTO.warehouseId, :modelDTO.name, :modelDTO.type," +
            " :modelDTO.details, :modelDTO.queryPK, :modelDTO.demo)")
    long createModel(@BindBean("modelDTO") ModelInputDTO modelDTO, @BindBean("user") User user);

    @GetGeneratedKeys
    @SqlUpdate("insert into query_models(team_id, warehouse_id, name ,type, details, query_pk, demo)" +
            " values(:teamId, :modelDTO.warehouseId, :modelDTO.name, :modelDTO.type," +
            " :modelDTO.details, :modelDTO.queryPK, :modelDTO.demo)")
    long createQueryModel(@BindBean("modelDTO") ModelInputDTO modelDTO, @Bind("teamId") Long teamId);

    @SqlQuery("select * from query_models where name = :name and is_deleted = 0")
    QueryModel getModelByName(@Bind("name") String name);

    @SqlQuery("select * from query_models where id = :id and is_deleted = 0")
    QueryModel getQueryModel(@Bind("id") Long id);


    @SqlQuery("select * from query_models where team_id =:teamId and is_deleted = 0 order by id desc")
    List<QueryModel> getQueryModelsByTeam(@Bind("teamId") Long teamId);

    @SqlQuery("select * from query_models where warehouse_id =:warehouseId and team_id =:teamId and is_deleted = 0 order by id desc")
    List<QueryModel> getQueryModelsByWarehouseAndTeam(@Bind("warehouseId") Long whId, @Bind("teamId") Long teamId);

    @SqlUpdate("update query_models set is_deleted = 1 where id = :id")
    void deleteModel(@Bind("id") Long id);

    @SqlQuery("select count(id) from query_models where is_deleted = 0 and demo=0 and team_id = :teamId")
    int getTotalActiveModelsForTeam(@Bind("teamId") Long teamId);

    @SqlQuery("select * from query_models where is_deleted = 0 and demo= 1 and team_id = :teamId")
    QueryModel getDemoModelForTeam(@Bind("teamId") Long teamId);

    class QueryModelArgumentFactory extends AbstractArgumentFactory<QueryModelDetails> {

        public QueryModelArgumentFactory() {
            super(Types.VARCHAR);
        }

        @Override
        protected Argument build(QueryModelDetails modelDetails, ConfigRegistry config) {
            return (position, statement, ctx) -> statement.setString(position, JsonUtils.objectToString(modelDetails));
        }
    }

    class QueryModelPKArgumentFactory extends AbstractArgumentFactory<QueryModelPK> {

        public QueryModelPKArgumentFactory() {
            super(Types.VARCHAR);
        }

        @Override
        protected Argument build(QueryModelPK queryModelPK, ConfigRegistry config) {
            return (position, statement, ctx) -> statement.setString(position, JsonUtils.objectToString(queryModelPK));
        }
    }

    class QueryModelRowMapper implements RowMapper<QueryModel> {

        @Override
        public QueryModel map(ResultSet rs, StatementContext ctx) throws SQLException {
            QueryModelPK queryModelPK = JsonUtils.jsonStringToObject(rs.getString(TableFields.WAREHOUSE_PK), QueryModelPK.class);
            QueryModelDetails modelDetails = JsonUtils.jsonStringToObject(rs.getString(TableFields.QUERY_MODEL_DETAILS), QueryModelDetails.class);

            return QueryModel.builder().id(rs.getLong(TableFields.ID)).name(rs.getString(TableFields.NAME))
                    .type(QueryModelType.valueOf(rs.getString(TableFields.TYPE))).teamId(rs.getLong(TableFields.TEAM_ID))
                    .details(modelDetails).warehouseId(rs.getLong(TableFields.WAREHOUSE_ID))
                    .queryPK(queryModelPK).demo(rs.getBoolean(TableFields.DEMO_MODEL)).build();
        }
    }
}
