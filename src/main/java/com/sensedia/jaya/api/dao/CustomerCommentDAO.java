package com.sensedia.jaya.api.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.sensedia.jaya.api.model.Comment;

@RegisterMapper(CustomerCommentDAO.CommentMapper.class)
public interface CustomerCommentDAO {

	@SqlUpdate("create table t_customer_comment( id mediumint not null primary key auto_increment, user_id varchar(32) not null, customer_id mediumint not null, txt_contents varchar(4000), dt_created timestamp )")
	void createTable();

	@GetGeneratedKeys
	@SqlUpdate("insert into t_customer_comment(customer_id, user_id, txt_contents, dt_created) values (:customerId, :c.userId, :c.text, :c.date)")
	Long insertComment(@Bind("customerId") String customerId, @BindBean("c") Comment u);

	@SqlQuery("select * from t_customer_comment where id = :it")
	Comment findById(@Bind long commentId);

	@SqlQuery("select * from t_customer_comment where customer_id = :it order by dt_created asc")
	List<Comment> findByCustomer(@Bind Long customerId);

	@SqlUpdate("delete from t_customer_comment where id = :it")
	void deleteById(@Bind long commentId);

	public static class CommentMapper implements ResultSetMapper<Comment> {
		public Comment map(int index, ResultSet r, StatementContext ctx) throws SQLException {
			return new Comment().setId(r.getLong("id")).setDate(new Date(r.getDate("dt_created").getTime()))
					.setText(r.getString("txt_contents")).setUserId(r.getString("user_id"));
		}
	}
}
