package com.sensedia.jaya.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.sensedia.jaya.api.access.RequestUser;
import com.sensedia.jaya.api.dao.OpinionDAO;
import com.sensedia.jaya.api.model.Opinion;
import com.sensedia.jaya.api.model.User;
import com.wordnik.swagger.annotations.Api;

@Path("/opinions")
@Produces("application/json")
@Api(value = "/opinions", description = "Operations that create, update, and list opinions.")
public class OpinionsResource {

	private OpinionDAO opinionDAO;

	public OpinionsResource(OpinionDAO opinionDAO) {
		this.opinionDAO = opinionDAO;
	}

	@POST
	@Consumes("application/json")
	public Response create(@RequestUser User u, Opinion opinion) {
		opinion.setId(null);
		opinion.setUserId(u.getUserId());
		Long id = opinionDAO.insert(opinion);
		return Response.status(Response.Status.CREATED).header("Location", id).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes("application/json")
	public Response update(@RequestUser User u, @PathParam("id") Long opinionId, Opinion opinion) {
		Opinion old = opinionDAO.findById(opinionId);
		if (old == null)
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build());

		if (!old.getUserId().equals(u.getUserId()))
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
					.entity("Attempt to change someone else's opinion").build());

		opinion.setId(opinionId);
		opinionDAO.update(opinion);
		return Response.status(Response.Status.ACCEPTED).build();
	}

}
