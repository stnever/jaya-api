package com.sensedia.jaya.api;

import org.apache.http.client.HttpClient;
import org.skife.jdbi.v2.DBI;

import com.sensedia.jaya.api.access.AccessResource;
import com.sensedia.jaya.api.access.RequestUserInjectable;
import com.sensedia.jaya.api.access.RequestUserProvider;
import com.sensedia.jaya.api.dao.CustomerCommentDAO;
import com.sensedia.jaya.api.dao.CustomerDAO;
import com.sensedia.jaya.api.dao.OpinionDAO;
import com.sensedia.jaya.api.dao.PainCommentDAO;
import com.sensedia.jaya.api.dao.UserDAO;
import com.sensedia.jaya.api.resources.CustomersResource;
import com.sensedia.jaya.api.resources.MyApiListingResourceJSON;
import com.sensedia.jaya.api.resources.OpinionsResource;
import com.sensedia.jaya.api.resources.PainsResource;
import com.wordnik.swagger.jaxrs.JaxrsApiReader;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.client.HttpClientBuilder;
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
		final DBI jdbi = factory.build(env, config.getDatabaseConfiguration(), "jaya");
		final UserDAO userDao = jdbi.onDemand(UserDAO.class);
		final CustomerDAO customerDao = jdbi.onDemand(CustomerDAO.class);
		final CustomerCommentDAO customerCommentDao = jdbi.onDemand(CustomerCommentDAO.class);
		final PainCommentDAO painCommentDao = jdbi.onDemand(PainCommentDAO.class);
		final OpinionDAO opinionDao = jdbi.onDemand(OpinionDAO.class);

		RequestUserInjectable requestUserInjectable = new RequestUserInjectable(userDao);
		env.addProvider(new RequestUserProvider(requestUserInjectable));

		HttpClient httpClient = new HttpClientBuilder().using(config.getHttpClientConfiguration()).build();
		
		env.addResource(new AccessResource(userDao, httpClient));
		env.addResource(new PainsResource(config.getJiraConfiguration(), painCommentDao, opinionDao, httpClient));
		env.addResource(new CustomersResource(customerDao, customerCommentDao, opinionDao));
		env.addResource(new OpinionsResource(opinionDao));
		env.addResource(new MyApiListingResourceJSON());
	}

}
