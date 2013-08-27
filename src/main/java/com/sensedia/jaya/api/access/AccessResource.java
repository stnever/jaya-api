package com.sensedia.jaya.api.access;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensedia.jaya.api.JayaConfiguration.GoogleConfiguration;
import com.sensedia.jaya.api.dao.UserDAO;
import com.sensedia.jaya.api.model.User;
import com.sensedia.jaya.api.utils.Input;
import com.sensedia.jaya.api.utils.Utils;

@Path("/access")
public class AccessResource {

	private UserDAO userDAO;

	private HttpClient httpClient;

	private GoogleConfiguration googleConfig;

	private static Logger _logger = LoggerFactory.getLogger(AccessResource.class.getName());

	public AccessResource(UserDAO userDAO, HttpClient client, GoogleConfiguration googleConfig) {
		super();
		this.userDAO = userDAO;
		this.httpClient = client;
		this.googleConfig = googleConfig;
	}

	@POST
	@Path("/connect")
	@Produces("text/plain")
	public Response connectToGoogle(String accessToken) {
		try {
			_logger.info("Attempting to connect accessToken {}", accessToken);

			// Obtem o user_id e o email a partir do tokeninfo
			JsonNode tokeninfo = get("https://www.googleapis.com/oauth2/v1/tokeninfo", "access_token", accessToken);
			String email = tokeninfo.has("email") ? tokeninfo.get("email").textValue() : null;
			String userId = tokeninfo.has("user_id") ? tokeninfo.get("user_id").textValue() : null;

			// valida se o usuário é de um domínio específico
			if (!Utils.isBlank(googleConfig.getRestrictDomain()) && !email.endsWith(googleConfig.getRestrictDomain())) {
				return Response.status(Status.FORBIDDEN).entity("unacceptable_domain").build();
			}

			// Obtem o client_id e valida que é igual ao nosso
			String clientId = tokeninfo.get("audience").textValue();
			if (!clientId.equals(googleConfig.getClientId())) {
				throw new RuntimeException("Token audience (" + clientId + ") does not match configured clientId ("
						+ googleConfig.getClientId() + ")");
			}

			// Obtem o nome a partir do /me
			JsonNode userinfo = get("https://www.googleapis.com/plus/v1/people/me", "access_token", accessToken);
			String name = userinfo.has("displayName") ? userinfo.get("displayName").textValue() : null;

			String image = userinfo.path("image").path("url").textValue();

			_logger.debug("User information: name {}, email {}, userId {}, imageUrl {}", name, email, userId, image);

			boolean sessionIdExists = true;
			String sessionId = null;
			while (sessionIdExists) {
				sessionId = new BigInteger(64, new SecureRandom()).toString(32);
				sessionIdExists = userDAO.checkIfSessionIdExists(sessionId) > 0;
			}

			_logger.debug("Generated sessionId: {}", sessionId);

			User u = userDAO.findById(userId);
			if (u == null) {
				u = new User().setEmail(email).setName(name).setUserId(userId).setSessionId(sessionId).setImageUrl(image);
				_logger.debug("User did not exist on the database, creating now: {}", u);
				userDAO.insert(u);
			} else {
				u.setSessionId(sessionId);
				u.setImageUrl(image);
				_logger.debug("Updating existing user: {}", u);
				userDAO.update(u);
			}

			return Response.status(Response.Status.OK).entity(sessionId).build();
		} catch (Exception e) {
			_logger.error("Error", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/disconnect")
	public Response disconnectFromGoogle(@RequestUser User u, String accessToken) throws ClientProtocolException,
			IOException {
		_logger.info("Disconnecting user {}", u);
		// O processo de logout envolve apenas apagar o sessionId do user (não
		// podemos apagar o user pois suas opiniões precisam ser mantidas)
		u.setSessionId(null);
		userDAO.update(u);

		if (accessToken != null) {
			_logger.info("Revoking access token {}", accessToken);

			HttpGet get = new HttpGet("https://accounts.google.com/o/oauth2/revoke?token=" + accessToken);
			HttpResponse resp = httpClient.execute(get);

			if (resp.getStatusLine().getStatusCode() != 200) {
				throw new WebApplicationException(Response.status(500)
						.entity("Received " + resp.getStatusLine() + " while disconnecting OAuth token").build());
			}
		}

		return Response.status(Response.Status.OK).build();
	}

	@GET
	@Path("/me")
	public Object getCurrentUserInfo(@RequestUser User u) {
		return Utils.makeMap("userId", u.getUserId(), "name", u.getName(), "email", u.getEmail());
	}

	private JsonNode get(String url, String... params) {
		try {
			URIBuilder builder = new URIBuilder(url);

			for (int i = 0; i < params.length; i += 2) {
				builder.setParameter(params[i], params[i + 1]);
			}

			HttpGet get = new HttpGet(builder.build());

			HttpResponse resp = httpClient.execute(get);

			if (resp.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Received " + resp.getStatusLine() + " for URL " + url);
			}

			String json = Input.stream(resp.getEntity().getContent()).readString();
			_logger.debug("Received: \n{}", json);
			return new ObjectMapper().readTree(json);
		} catch (Exception e) {
			_logger.error("Error", e);
			throw new RuntimeException(e);
		}
	}
}
