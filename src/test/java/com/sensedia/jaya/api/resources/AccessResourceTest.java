package com.sensedia.jaya.api.resources;

import junit.framework.TestCase;

import org.apache.http.impl.client.DefaultHttpClient;
import org.skife.jdbi.v2.DBI;

import com.sensedia.jaya.api.JayaConfiguration.GoogleConfiguration;
import com.sensedia.jaya.api.access.AccessResource;
import com.sensedia.jaya.api.dao.UserDAO;
import com.sensedia.jaya.api.model.User;
import com.sensedia.jaya.api.utils.Utils;

public class AccessResourceTest extends TestCase {

	String testAccessToken = null;

	private UserDAO userDAO;
	private AccessResource accessResource;

	@Override
	protected void setUp() throws Exception {
		DBI dbi = new DBI("jdbc:mysql://localhost:3306/kratoa-db", "root", "");
		this.userDAO = dbi.onDemand(UserDAO.class);
		this.accessResource = new AccessResource(userDAO, new DefaultHttpClient(), new GoogleConfiguration());
	}

	public void testAccess() throws Exception {
		// userDAO.createTable();

		if (testAccessToken == null)
			return;

		String s = accessResource.connectToGoogle(testAccessToken).getEntity().toString();
		assertFalse(Utils.isBlank(s));
		System.out.println(s);

		User u = userDAO.findBySessionId(s);
		assertNotNull(u);

		String o = accessResource.getCurrentUserInfo(u).toString();
		assertFalse(Utils.isBlank(o));
		System.out.println(o);

		accessResource.disconnectFromGoogle(u);
		u = userDAO.findById(u.getUserId());
		assertTrue(Utils.isBlank(u.getSessionId()));

		userDAO.deleteById(u.getUserId());
	}

}
