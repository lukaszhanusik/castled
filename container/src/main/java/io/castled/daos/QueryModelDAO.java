package io.castled.daos;

import io.castled.constants.TableFields;
import io.castled.dtos.querymodel.QueryModelDTO;
import io.castled.dtos.querymodel.QueryModelDetails;
import io.castled.models.QueryModel;
import io.castled.models.QueryModelPK;
import io.castled.models.users.User;
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

@RegisterRowMapper(QueryModelDAO.QueryModelRowMapper.class)
public interface QueryModelDAO {

    @GetGeneratedKeys
    @SqlUpdate("insert into query_model(id, user_id, team_id,warehouse_id, model_name,model_details,query_pk)" +
            " values(:modelDTO.id, :user.id, :user.teamId, :modelDTO.warehouseId, :modelDTO.modelName," +
            " :modelDTO.modelDetails, :modelDTO.queryModelPK)")
    long createModel(@BindBean("pipeline") QueryModelDTO modelDTO, @BindBean("user") User user);

    @SqlQuery("select * from query_model where model_name = :model_name and is_deleted = 0")
    QueryModel getQueryModelByModelName(@Bind("modelName") String modelName);

    @SqlQuery("select * from query_model where id = :id and is_deleted = 0")
    QueryModel getQueryModel(@Bind("id") Long id);

    @SqlQuery("select * from query_model where warehouse_id =:whId and is_deleted = 0")
    List<QueryModel> getQueryModelsByWarehouse(@Bind("whId") Long whId);

    @SqlQuery("select * from query_model where team_id =:teamId and is_deleted = 0")
    List<QueryModel> getQueryModelsByTeam(@Bind("teamId") Long teamId);

    @SqlQuery("select * from query_model where user_id =:userId and is_deleted = 0")
    List<QueryModel> getQueryModelsByUser(@Bind("userId") Long userId);

    @SqlQuery("select * from query_model where warehouse_id =:whId and team_id =:teamId and is_deleted = 0")
    List<QueryModel> getQueryModelsByWarehouseAndTeam(@Bind("whId") Long whId, @Bind("teamId") Long teamId);

    @SqlUpdate("update query_model set is_deleted = 1 where id = :id")
    void deleteModel(@Bind("id") Long id);

    class QueryModelRowMapper implements RowMapper<QueryModel> {

        @Override
        public QueryModel map(ResultSet rs, StatementContext ctx) throws SQLException {
            QueryModelPK queryModelPK = JsonUtils.jsonStringToObject(rs.getString(TableFields.WAREHOUSE_PK), QueryModelPK.class);
            QueryModelDetails modelDetails = JsonUtils.jsonStringToObject(rs.getString(TableFields.QUERY_MODEL_DETAILS), QueryModelDetails.class);

            return QueryModel.builder().id(rs.getLong(TableFields.ID)).modelName(rs.getString(TableFields.MODEL_NAME))
                    .teamId(rs.getLong(TableFields.TEAM_ID)).modelDetails(modelDetails).warehouseId(rs.getLong(TableFields.WAREHOUSE_ID))
                    .queryModelPK(queryModelPK).build();
        }
    }
}
