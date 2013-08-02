package com.sensedia.jaya.api.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface PainDAO {

	@SqlUpdate("create table t_pain (id int primary key, name varchar(100))")
	void createPainTable();

	@SqlUpdate("insert into t_pain(id, name) values (:id, :name)")
	void insert(@Bind("id") int id, @Bind("name") String name);

	@SqlQuery("select name from t_pain where id = :id")
	String findNameById(@Bind("id") int id);

}
