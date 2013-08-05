package com.sensedia.jaya.api.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sensedia.jaya.api.JayaConfiguration.JiraConfiguration;
import com.sensedia.jaya.api.access.RequestUser;
import com.sensedia.jaya.api.dao.OpinionDAO;
import com.sensedia.jaya.api.dao.PainCommentDAO;
import com.sensedia.jaya.api.model.Pain;
import com.sensedia.jaya.api.model.User;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/pains")
@Produces("application/json")
@Api(value = "/pains", description = "Operations that create, update, and list Pains.")
public class PainsResource {

	private JiraConfiguration jiraConfiguration;

	private PainCommentDAO commentDAO;

	private OpinionDAO opinionDAO;

	private HttpClient httpClient;

	public PainsResource(JiraConfiguration jiraConfiguration, PainCommentDAO commentDAO, OpinionDAO opinionDAO,
			HttpClient httpClient) {
		super();
		this.jiraConfiguration = jiraConfiguration;
		this.commentDAO = commentDAO;
		this.opinionDAO = opinionDAO;
		this.httpClient = httpClient;
	}

	private static Logger _logger = LoggerFactory.getLogger(PainsResource.class.getName());

	@GET
	public List<Pain> findAll(@RequestUser User u) {
		return null;
	}

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Find pain by ID", notes = "Add extra notes here", responseClass = "com.sensedia.jaya.api.Pain")
	public Pain findById(@RequestUser User u,
			@ApiParam(value = "ID of Pain to fetch", required = true) @PathParam("id") String id) {
		_logger.info("Retrieving Pain #{}", id);
		return new Pain().setId(id);
	}

	@POST
	@Consumes("application/json")
	public Response create(@RequestUser User u, Pain pain) {
		return null;
	}

	@PUT
	@Consumes("application/json")
	public Response update(@RequestUser User u) {
		return null;
	}

}
