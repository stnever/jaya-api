package com.sensedia.jaya.api.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.sensedia.jaya.api.model.Opinion;

@RegisterMapper(OpinionDAO.OpinionMapper.class)
public interface OpinionDAO {

	@SqlUpdate("insert into t_opinion(id, user_id, pain_id, customer_id, value, comment ) values (:id, :userId, :painId, :customerId, value, comment)")
	@GetGeneratedKeys
	Long insert(@BindBean Opinion u);

	@SqlUpdate("update t_opinion set value = :value, comment = :comment where id = :id")
	void update(@BindBean Opinion u);

	@SqlQuery("select * from t_opinion where id = :it")
	Opinion findById(@Bind long OpinionId);

	@SqlQuery("select * from t_opinion where customer_id = :it")
	List<Opinion> findByCustomer(@Bind Long customerId);

	@SqlQuery("select * from t_opinion where pain_id = :it")
	List<Opinion> findByPain(@Bind String painId);

	@SqlQuery("select * from t_opinion where user_id = :it")
	List<Opinion> findByUser(@Bind Long userId);

	@SqlQuery("select * from t_opinion")
	List<Opinion> findAll();

	@SqlUpdate("delete from t_opinion where id = :it")
	void deleteById(@Bind long id);

	public static class OpinionMapper implements ResultSetMapper<Opinion> {

		public Opinion map(int index, ResultSet r, StatementContext ctx) throws SQLException {
			return new Opinion().setId(r.getLong("id")).setPainId(r.getString("pain_id"))
					.setCustomerId(r.getLong("customer_id")).setUserId(r.getLong("user_id"))
					.setValue(r.getInt("value")).setComment(r.getString("comment"));
		}
	}
}
