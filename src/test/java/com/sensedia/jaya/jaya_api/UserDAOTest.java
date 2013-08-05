package com.sensedia.jaya.jaya_api;

import java.util.List;

import org.skife.jdbi.v2.DBI;

import com.sensedia.jaya.api.dao.UserDAO;
import com.sensedia.jaya.api.model.User;

import junit.framework.TestCase;

public class UserDAOTest extends TestCase {

	UserDAO dao = null;

	@Override
	protected void setUp() throws Exception {
		DBI dbi = new DBI("jdbc:mysql://localhost:3306/kratoa-db", "root", "");
		this.dao = dbi.onDemand(UserDAO.class);
	}
	
	public void testDAO() throws Exception {
		User u = new User().setUserId(12345L).setName("bob").setEmail("bob@somewhere").setSessionId("abc");
		dao.insert(u);
		
		User u2 = dao.findById(12345L);
		assertNotNull(u2);
		
		u2.setUserId(444L);
		u2.setSessionId("def");
		dao.insert(u2);
		
		List<User> l = dao.findAll();
		System.out.println(l);
		assertEquals( 2, l.size());
		
		dao.deleteById(12345L);
		dao.deleteById(444L);
		
		l = dao.findAll();
		assertTrue(l.isEmpty());
	}

}
