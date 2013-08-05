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

import com.sensedia.jaya.api.model.Customer;

@RegisterMapper(CustomerDAO.CustomerMapper.class)
public interface CustomerDAO {

	@GetGeneratedKeys
	@SqlUpdate("insert into t_customer(id, name, description) values (:id, :name, :description)")
	Long insert(@BindBean Customer u);

	@SqlUpdate("update t_customer set name = :name, description = :email where id = :id")
	void update(@BindBean Customer u);

	@SqlQuery("select * from t_customer where id = :it")
	Customer findById(@Bind long customerId);

	@SqlQuery("select * from t_customer order by name")
	List<Customer> findAll();

	@SqlUpdate("delete from t_customer where id = :it")
	void deleteById(@Bind long customerId);

	public static class CustomerMapper implements ResultSetMapper<Customer> {
		public Customer map(int index, ResultSet r, StatementContext ctx) throws SQLException {
			return new Customer().setId(r.getLong("id")).setName(r.getString("name"))
					.setDescription(r.getString("description"));
		}
	}
}
