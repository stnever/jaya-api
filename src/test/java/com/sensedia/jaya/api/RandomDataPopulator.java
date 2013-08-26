package com.sensedia.jaya.api;

import java.io.File;
import java.util.Random;

import org.apache.http.impl.client.DefaultHttpClient;
import org.skife.jdbi.v2.DBI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sensedia.jaya.api.dao.CustomerDAO;
import com.sensedia.jaya.api.dao.OpinionDAO;
import com.sensedia.jaya.api.dao.PainDAO;
import com.sensedia.jaya.api.dao.UserDAO;
import com.sensedia.jaya.api.model.Customer;
import com.sensedia.jaya.api.model.Opinion;
import com.sensedia.jaya.api.model.Pain;
import com.sensedia.jaya.api.model.User;
import com.sensedia.jaya.api.resources.PainsResource;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.jdbi.DBIFactory;

public class RandomDataPopulator {

	static UserDAO userDAO;
	static CustomerDAO customerDAO;
	static OpinionDAO opinionDAO;
	static PainsResource painsResource;
	static PainDAO painDAO;

	public static void main(String[] args) throws Exception {
		JayaConfiguration config = new ObjectMapper(new YAMLFactory()).readValue(new File("./testing.yaml"),
				JayaConfiguration.class);
		final DBIFactory factory = new DBIFactory();
		final DBI dbi = factory.build(new Environment("testing", config, null, null),
				config.getDatabaseConfiguration(), "jaya");
		opinionDAO = dbi.onDemand(OpinionDAO.class);
		customerDAO = dbi.onDemand(CustomerDAO.class);
		userDAO = dbi.onDemand(UserDAO.class);
		painDAO = new PainDAO(config.getJiraConfiguration(), new DefaultHttpClient());
		painsResource = new PainsResource(null, opinionDAO, customerDAO, painDAO);

		deleteAll();
		createUsers();
		createCustomers();
		createOpinions();
	}

	public static void deleteAll() {
		for (Opinion o : opinionDAO.findAll())
			opinionDAO.deleteById(o.getId());

		for (Customer c : customerDAO.findAll())
			customerDAO.deleteById(c.getId());

		for (User u : userDAO.findAll())
			userDAO.deleteById(u.getUserId());
	}

	public static void createUsers() {
		userDAO.insert(new User().setEmail("a@example.com").setName("Alice").setUserId("1"));
		userDAO.insert(new User().setEmail("b@example.com").setName("Bob").setUserId("2"));
		userDAO.insert(new User().setEmail("c@example.com").setName("Charlie").setUserId("3"));
		userDAO.insert(new User().setEmail("d@example.com").setName("Diane").setUserId("4"));
	}

	public static void createCustomers() {
		customerDAO.insert(new Customer().setName("Conta Azul").setDescription("Lorem ipsum dolor"));
		customerDAO.insert(new Customer().setName("Luna-Luna").setDescription("Lorem ipsum dolor"));
		customerDAO.insert(new Customer().setName("MOIP").setDescription("Lorem ipsum dolor"));
		customerDAO.insert(new Customer().setName("Serasa").setDescription("Lorem ipsum dolor"));
	}

	public static void createOpinions() {
		Random random = new Random();

		for (User u : userDAO.findAll()) {
			for (Customer c : customerDAO.findAll()) {

				// Chance de não comentar este customer
				if (random.nextDouble() < .4)
					continue;

				for (Pain p : painsResource.findAll(null)) {

					// Chance de não comentar esta pain
					if (random.nextDouble() < .2)
						continue;

					int value = random.nextInt(4) + 1;
					opinionDAO.insert(new Opinion().setCustomerId(c.getId()).setPainId(p.getId())
							.setUserId(u.getUserId()).setValue(value));
				}
			}
		}
	}
}
