package com.sensedia.jaya.api.resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensedia.jaya.api.JayaConfiguration.JiraConfiguration;
import com.sensedia.jaya.api.access.RequestUser;
import com.sensedia.jaya.api.dao.OpinionDAO;
import com.sensedia.jaya.api.dao.PainCommentDAO;
import com.sensedia.jaya.api.model.Comment;
import com.sensedia.jaya.api.model.Opinion;
import com.sensedia.jaya.api.model.Pain;
import com.sensedia.jaya.api.model.User;
import com.sensedia.jaya.api.utils.Base64;
import com.sensedia.jaya.api.utils.Input;
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
		_logger.info("Retrieving all pains");
		JsonNode json = get("/search", "maxResults", "100", "fields", "summary", "jql", "project=PG");
		List<Pain> result = new ArrayList<Pain>();

		for (JsonNode jsonIssue : json.findValues("issues")) {
			Pain pain = new Pain().setDescription(jsonIssue.get("description").textValue())
					.setId(jsonIssue.get("key").textValue()).setTitle(jsonIssue.get("summary").textValue());
			result.add(pain);
		}

		return result;
	}

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Find pain by ID", notes = "Add extra notes here", responseClass = "com.sensedia.jaya.api.Pain")
	public Pain findById(@RequestUser User u,
			@ApiParam(value = "ID of Pain to fetch", required = true) @PathParam("id") String id) {
		_logger.info("Retrieving Pain {}", id);

		JsonNode jsonIssue = get("/issue/" + id);
		Pain pain = new Pain().setDescription(jsonIssue.get("description").textValue())
				.setId(jsonIssue.get("key").textValue()).setTitle(jsonIssue.get("summary").textValue());

		List<Comment> comments = commentDAO.findByPain(pain.getId());
		if (comments != null)
			pain.setComments(comments);

		List<Opinion> opinions = opinionDAO.findByPain(pain.getId());
		if (opinions != null)
			pain.setOpinions(opinions);

		return pain;
	}

	@POST
	@Consumes("application/json")
	public Response create(@RequestUser User u, Pain pain) {
		_logger.info("Creating new pain: {}", pain);
		return null;
	}

	@PUT
	@Consumes("application/json")
	public Response update(@RequestUser User u, Pain pain) {
		_logger.info("Updating pain: {}", pain);
		return null;
	}

	@POST
	@Path("/{id}/comments")
	public Response addComment(@RequestUser User u, @PathParam("id") String painId, String comment) {
		_logger.info("Adding comment to pain {} by user {}: {}", painId, u, comment);
		Comment c = new Comment().setDate(new Date()).setText(comment).setUserId(u.getUserId());
		commentDAO.insertComment(painId, c);
		return Response.status(Response.Status.OK).build();
	}

	@PUT
	@Path("/{painId}/opinions/{customerId}")
	public Response addComment(@RequestUser User u, @PathParam("painId") String painId,
			@PathParam("customerId") Long customerId, Integer value, String comment) {
		_logger.info("Adding opinion to pain {}, customer {}, by user {}: {} {}", painId, customerId, u, value, comment);

		Opinion o = opinionDAO.findByKey(painId, customerId, u.getUserId());
		if (o == null) {
			o = new Opinion().setComment(comment).setValue(value).setPainId(painId).setCustomerId(customerId)
					.setUserId(u.getUserId());
			opinionDAO.insert(o);
		} else {
			o.setValue(value).setComment(comment);
			opinionDAO.update(o);
		}
		return Response.status(Response.Status.OK).build();
	}

	private JsonNode get(String resource, String... params) {
		HttpGet get = new HttpGet(jiraConfiguration.getJiraApiRoot() + resource);
		HttpParams httpParams = get.getParams();
		for (int i = 0; i < params.length; i += 2) {
			httpParams.setParameter(params[i], params[i + 1]);
		}

		// set up authorization header
		String base64 = Base64
				.encodeLines((jiraConfiguration.getJiraUser() + ":" + jiraConfiguration.getJiraPassword()).getBytes());
		httpParams.setParameter("Authorization", "Basic " + base64);

		try {
			HttpResponse resp = httpClient.execute(get);
			String json = Input.stream(resp.getEntity().getContent()).readString();
			return new ObjectMapper().readTree(json);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
