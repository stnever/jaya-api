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
import com.sensedia.jaya.api.dao.PainDAO;
import com.sensedia.jaya.api.dao.UserDAO;
import com.sensedia.jaya.api.resources.CustomersResource;
import com.sensedia.jaya.api.resources.LeaderboardResource;
import com.sensedia.jaya.api.resources.MyApiListingResourceJSON;
import com.sensedia.jaya.api.resources.OpinionsResource;
import com.sensedia.jaya.api.resources.PainsResource;
import com.sensedia.jaya.api.resources.UnlinkedIssuesResource;
import com.sensedia.jaya.api.resources.UsersResource;
import com.thetransactioncompany.cors.CORSFilter;
import com.wordnik.swagger.jaxrs.JaxrsApiReader;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.client.HttpClientBuilder;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.jdbi.DBIFactory;

public class JayaService extends Service<JayaConfiguration> {

	public static void main(String[] args) throws Exception {
		JaxrsApiReader.setFormatString("");
		if (args == null || args.length < 1)
			new JayaService().run(new String[] { "server", "./testing.yaml" });
		else
			new JayaService().run(args);
	}

	@Override
	public void initialize(Bootstrap<JayaConfiguration> bootstrap) {
		bootstrap.setName("jaya");
		bootstrap.addBundle(new AssetsBundle("/web/", "/", "index.html"));
	}

	@Override
	public void run(JayaConfiguration config, Environment env) throws Exception {
		HttpClient httpClient = new HttpClientBuilder().using(config.getHttpClientConfiguration()).build();

		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(env, config.getDatabaseConfiguration(), "jaya");
		final UserDAO userDAO = jdbi.onDemand(UserDAO.class);
		final CustomerDAO customerDAO = jdbi.onDemand(CustomerDAO.class);
		final CustomerCommentDAO customerCommentDAO = jdbi.onDemand(CustomerCommentDAO.class);
		final PainCommentDAO painCommentDAO = jdbi.onDemand(PainCommentDAO.class);
		final OpinionDAO opinionDAO = jdbi.onDemand(OpinionDAO.class);
		final PainDAO painDAO = new PainDAO(config.getJiraConfiguration(), httpClient);

		RequestUserInjectable requestUserInjectable = new RequestUserInjectable(userDAO);
		env.addProvider(new RequestUserProvider(requestUserInjectable));

		env.addResource(new AccessResource(userDAO, httpClient, config.getGoogleConfiguration()));
		env.addResource(new PainsResource(painCommentDAO, opinionDAO, customerDAO, painDAO));
		env.addResource(new CustomersResource(customerDAO, customerCommentDAO, opinionDAO, painDAO));
		env.addResource(new OpinionsResource(opinionDAO));
		env.addResource(new MyApiListingResourceJSON());
		env.addResource(new UnlinkedIssuesResource(httpClient, config.getJiraConfiguration()));
		env.addResource(new UsersResource(userDAO));
		env.addResource(new LeaderboardResource(customerDAO, painDAO, opinionDAO));

		env.addFilter(CORSFilter.class, config.getHttpConfiguration().getRootPath())
				.setInitParam("allowedOrigins", "*")
				.setInitParam("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");

	}

}
