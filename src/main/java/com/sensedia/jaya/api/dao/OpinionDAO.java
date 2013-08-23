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

	@SqlUpdate("create table t_opinion( id mediumint not null primary key auto_increment, user_id varchar(32) not null, pain_id varchar(32) not null, customer_id numeric(5) not null, value numeric(3) not null, comment varchar(4000) )")
	void createTable();

	@SqlUpdate("insert into t_opinion(id, user_id, pain_id, customer_id, value, comment ) values (:id, :userId, :painId, :customerId, :value, :comment)")
	@GetGeneratedKeys
	Long insert(@BindBean Opinion u);

	@SqlUpdate("update t_opinion set value = :value, comment = :comment where id = :id")
	void update(@BindBean Opinion u);

	@SqlQuery("select o.*, u.name as user_login from t_opinion o, t_user u where o.user_id = u.user_id and o.id = :it")
	Opinion findById(@Bind long OpinionId);

	@SqlQuery("select o.*, u.name as user_login from t_opinion o, t_user u where o.user_id = u.user_id and o.customer_id = :it")
	List<Opinion> findByCustomer(@Bind Long customerId);

	@SqlQuery("select o.*, u.name as user_login from t_opinion o, t_user u where o.user_id = u.user_id and o.pain_id = :it")
	List<Opinion> findByPain(@Bind String painId);

	@SqlQuery("select o.*, u.name as user_login from t_opinion o, t_user u where o.user_id = u.user_id and o.user_id = :it")
	List<Opinion> findByUser(@Bind String userId);

	@SqlQuery("select o.*, u.name as user_login from t_opinion o, t_user u where o.user_id = u.user_id")
	List<Opinion> findAll();

	@SqlQuery("select o.*, u.name as user_login from t_opinion o, t_user u where o.user_id = u.user_id and o.pain_id = :painId and o.customer_id = :customerId and o.user_id = :userId")
	Opinion findByKey(@Bind("painId") String painId, @Bind("customerId") Long customerId, @Bind("userId") String userId);

	@SqlUpdate("delete from t_opinion where id = :it")
	void deleteById(@Bind long id);

	public static class OpinionMapper implements ResultSetMapper<Opinion> {

		public Opinion map(int index, ResultSet r, StatementContext ctx) throws SQLException {
			return new Opinion().setId(r.getLong("id")).setPainId(r.getString("pain_id"))
					.setCustomerId(r.getLong("customer_id")).setUserId(r.getString("user_id"))
					.setValue(r.getInt("value")).setComment(r.getString("comment")).setUserLogin(r.getString("user_login"));
		}
	}
}
