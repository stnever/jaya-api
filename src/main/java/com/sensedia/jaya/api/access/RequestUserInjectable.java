package com.sensedia.jaya.api.access;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sensedia.jaya.api.dao.UserDAO;
import com.sensedia.jaya.api.model.User;
import com.sensedia.jaya.api.utils.Utils;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;

public class RequestUserInjectable extends AbstractHttpContextInjectable<User> {

	private static Logger _logger = LoggerFactory.getLogger(RequestUserInjectable.class.getName());

	UserDAO dao;

	public RequestUserInjectable(UserDAO dao) {
		super();
		this.dao = dao;
	}

	@Override
	public User getValue(HttpContext httpContext) {
		String sessionId = httpContext.getRequest().getHeaderValue("X-Session-ID");
		if ( Utils.isBlank(sessionId) ) {
			sessionId = httpContext.getRequest().getCookieNameValueMap().getFirst("X-Session-ID");
		}
		_logger.debug("Received sessionId: {}");

		if (Utils.isBlank(sessionId)) {
			fail("Missing X-Session-ID.");
		}

		User u = dao.findBySessionId(sessionId);
		if (u == null)
			fail("Invalid X-Session-ID.");

		_logger.debug("Current user: {}", u);
		return u;
	}

	private void fail(String msg) {
		_logger.error(msg);
		throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity(msg)
				.type(MediaType.TEXT_PLAIN_TYPE).build());
	}
}
