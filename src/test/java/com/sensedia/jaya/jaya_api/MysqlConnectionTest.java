package com.sensedia.jaya.jaya_api;

import java.sql.DriverManager;

public class MysqlConnectionTest {

	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		DriverManager.getConnection("jdbc:mysql://localhost:3306/kratoa-db", "root", "");
	}
}
