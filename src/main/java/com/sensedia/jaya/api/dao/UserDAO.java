package com.sensedia.jaya.api.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.sensedia.jaya.api.model.User;

@RegisterMapper(UserDAO.UserMapper.class)
public interface UserDAO {
	
	@SqlUpdate("create table t_user( user_id varchar(32) not null primary key, name varchar(256) not null, email varchar(256), session_id varchar(32) )")
	void createTable();

	@SqlUpdate("insert into t_user(user_id, name, email, session_id) values (:userId, :name, :email, :sessionId)")
	void insert(@BindBean User u);

	@SqlUpdate("update t_user set name = :name, email = :email, session_id = :sessionId where user_id = :userId")
	void update(@BindBean User u);

	@SqlQuery("select * from t_user where user_id = :it")
	User findById(@Bind String userId);

	@SqlQuery("select * from t_user where session_id = :it")
	User findBySessionId(@Bind String sessionId);

	@SqlQuery("select * from t_user order by name")
	List<User> findAll();

	@SqlUpdate("delete from t_user where user_id = :it")
	void deleteById(@Bind String userId);

	@SqlQuery("select count(*) from t_user where session_id = :it")
	Integer checkIfSessionIdExists(@Bind String sessionId);

	public static class UserMapper implements ResultSetMapper<User> {

		public User map(int index, ResultSet r, StatementContext ctx) throws SQLException {
			return new User().setUserId(r.getString("user_id")).setName(r.getString("name"))
					.setEmail(r.getString("email")).setSessionId(r.getString("session_id"));
		}
	}
}
