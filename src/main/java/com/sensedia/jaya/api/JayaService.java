package com.sensedia.jaya.api;


import org.skife.jdbi.v2.DBI;

import com.sensedia.jaya.api.dao.PainDAO;
import com.sensedia.jaya.api.resources.MyApiListingResourceJSON;
import com.sensedia.jaya.api.resources.PainResource;
import com.wordnik.swagger.jaxrs.JaxrsApiReader;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.jdbi.DBIFactory;

public class JayaService extends Service<JayaConfiguration> {

	public static void main(String[] args) throws Exception {
		JaxrsApiReader.setFormatString("");
		new JayaService().run(args);
	}

	@Override
	public void initialize(Bootstrap<JayaConfiguration> bootstrap) {
		bootstrap.setName("jaya");
	}

	@Override
	public void run(JayaConfiguration config, Environment env) throws Exception {
		final DBIFactory factory = new DBIFactory();
	    final DBI jdbi = factory.build(env, config.getDatabaseConfiguration(), "derby");
	    final PainDAO dao = jdbi.onDemand(PainDAO.class);
		env.addResource(new PainResource(dao));
		env.addResource(new MyApiListingResourceJSON());
	}

}
