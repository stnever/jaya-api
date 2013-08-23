package com.sensedia.jaya.api.resources;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.apache.http.impl.client.DefaultHttpClient;
import org.skife.jdbi.v2.DBI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sensedia.jaya.api.JayaConfiguration;
import com.sensedia.jaya.api.dao.CustomerDAO;
import com.sensedia.jaya.api.dao.OpinionDAO;
import com.sensedia.jaya.api.dao.PainCommentDAO;
import com.sensedia.jaya.api.model.Opinion;
import com.sensedia.jaya.api.model.Pain;
import com.sensedia.jaya.api.model.User;

public class PainsResourceTest extends TestCase {

	private PainsResource painsResource = null;
	private PainCommentDAO painCommentDAO = null;
	private OpinionDAO opinionDAO = null;

	@Override
	protected void setUp() throws Exception {
		DBI dbi = new DBI("jdbc:mysql://localhost:3306/jaya-db", "root", "");
		this.painCommentDAO = dbi.onDemand(PainCommentDAO.class);
		this.opinionDAO = dbi.onDemand(OpinionDAO.class);
		CustomerDAO customerDAO = dbi.onDemand(CustomerDAO.class);

		JayaConfiguration config = new ObjectMapper(new YAMLFactory()).readValue(new File("./testing.yaml"),
				JayaConfiguration.class);
		painsResource = new PainsResource(config.getJiraConfiguration(), painCommentDAO, opinionDAO,
				new DefaultHttpClient(), customerDAO);
	}

	public void testFindPains() throws Exception {
		List<Pain> pains = painsResource.findAll(null);
		assertNotNull(pains);
		assertTrue(pains.size() > 0);
		System.out.println(pains);

		Pain p = painsResource.findById(null, "PG-4");
		System.out.println(p);
		assertNotNull(p);
		assertEquals("PG-4", p.getId());
		assertTrue(p.getDescription().length() > 0);
		assertTrue(p.getTitle().length() > 0);
	}

	public void testAddOpinion() throws Exception {
		// opinionDAO.createTable();
		painsResource.addOpinion(new User().setUserId("ventura"), "PG-4", 123L, 3, "bla bla");
		Opinion o = opinionDAO.findByKey("PG-4", 123L, "ventura");
		Long oId = o.getId();
		assertNotNull(o);
		assertEquals(o.getValue(), (Integer) 3);

		painsResource.addOpinion(new User().setUserId("ventura"), "PG-4", 123L, 4, "blo blo");
		o = opinionDAO.findByKey("PG-4", 123L, "ventura");
		assertNotNull(o);
		assertEquals(o.getValue(), (Integer) 4);
		assertEquals(oId, o.getId());
	}
}
