package com.sensedia.jaya.api.resources;

import java.util.List;

import javax.ws.rs.core.Response;

import junit.framework.TestCase;

import org.skife.jdbi.v2.DBI;

import com.sensedia.jaya.api.dao.CustomerCommentDAO;
import com.sensedia.jaya.api.dao.CustomerDAO;
import com.sensedia.jaya.api.dao.OpinionDAO;
import com.sensedia.jaya.api.model.Customer;
import com.sensedia.jaya.api.model.Opinion;
import com.sensedia.jaya.api.model.User;
import com.sensedia.jaya.api.utils.Utils;

public class CustomersResourceTest extends TestCase {

	private CustomersResource customersResource = null;
	private CustomerCommentDAO customerCommentDAO = null;
	private OpinionDAO opinionDAO = null;
	private CustomerDAO customerDAO = null;

	@Override
	protected void setUp() throws Exception {
		DBI dbi = new DBI("jdbc:mysql://localhost:3306/kratoa-db", "root", "");
		this.customerCommentDAO = dbi.onDemand(CustomerCommentDAO.class);
		this.opinionDAO = dbi.onDemand(OpinionDAO.class);
		this.customerDAO = dbi.onDemand(CustomerDAO.class);
		customersResource = new CustomersResource(customerDAO, customerCommentDAO, opinionDAO);
	}

	public void testFindAll() throws Exception {

		Customer c = new Customer().setName("Produto Interno").setDescription("Capta as opiniões internas");
		Response r = customersResource.create(null, c);
		String strId = (String) r.getMetadata().getFirst("Location");
		assertFalse(Utils.isBlank(strId));

		List<Customer> pains = customersResource.findAll(null);
		assertNotNull(pains);
		assertTrue(pains.size() > 0);
		System.out.println(pains);

		Customer p = customersResource.findById(null, Long.valueOf(strId));
		System.out.println(p);
		assertNotNull(p);
	}

	public void testAddOpinion() throws Exception {
		customersResource.addOpinion(new User().setUserId("ventura"), "PG-4", 123L,
				Utils.makeStrMap("value", "3", "comment", "bla bla"));
		Opinion o = opinionDAO.findByKey("PG-4", 123L, "ventura");
		Long oId = o.getId();
		assertNotNull(o);
		assertEquals(o.getValue(), (Integer) 3);

		customersResource.addOpinion(new User().setUserId("ventura"), "PG-4", 123L,
				Utils.makeStrMap("value", "4", "comment", "blo blo"));
		o = opinionDAO.findByKey("PG-4", 123L, "ventura");
		assertNotNull(o);
		assertEquals(o.getValue(), (Integer) 4);
		assertEquals(oId, o.getId());
	}

}
