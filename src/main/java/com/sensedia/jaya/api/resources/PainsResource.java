package com.sensedia.jaya.api.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sensedia.jaya.api.access.RequestUser;
import com.sensedia.jaya.api.dao.CustomerDAO;
import com.sensedia.jaya.api.dao.OpinionDAO;
import com.sensedia.jaya.api.dao.PainCommentDAO;
import com.sensedia.jaya.api.dao.PainDAO;
import com.sensedia.jaya.api.model.AggregateResult;
import com.sensedia.jaya.api.model.Comment;
import com.sensedia.jaya.api.model.Opinion;
import com.sensedia.jaya.api.model.Pain;
import com.sensedia.jaya.api.model.User;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/pains")
@Produces("application/json")
@Api(value = "/pains", description = "Operations that create, update, and list Pains.")
public class PainsResource {

	private PainCommentDAO commentDAO;

	private OpinionDAO opinionDAO;

	private CustomerDAO customerDAO;

	private PainDAO painDAO;

	public PainsResource(PainCommentDAO commentDAO, OpinionDAO opinionDAO, CustomerDAO customerDAO, PainDAO painDAO) {
		super();
		this.commentDAO = commentDAO;
		this.opinionDAO = opinionDAO;
		this.customerDAO = customerDAO;
		this.painDAO = painDAO;
	}

	private static Logger _logger = LoggerFactory.getLogger(PainsResource.class.getName());

	@GET
	public List<Pain> findAll(@RequestUser User u) {
		return painDAO.findAll();
	}

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Find pain by ID", notes = "Add extra notes here", responseClass = "com.sensedia.jaya.api.Pain")
	public Pain findById(@RequestUser User u,
			@ApiParam(value = "ID of Pain to fetch", required = true) @PathParam("id") String id) {
		_logger.info("Retrieving Pain {}", id);

		Pain pain = painDAO.findById(id);

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
	public Response addOpinion(@RequestUser User u, @PathParam("painId") String painId,
			@PathParam("customerId") Long customerId, Map<String, String> data) {
		Integer value = Integer.valueOf(data.get("value"));
		String comment = data.get("comment");

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

	@GET
	@Path("{painId}/results")
	public List<AggregateResult> calculateAggregateResults(@PathParam("painId") String painId) {
		Map<Long, AggregateResult> aggregates = new HashMap<Long, AggregateResult>();

		String painName = painDAO.findById(painId).getTitle();

		List<Opinion> painOpinions = opinionDAO.findByPain(painId);
		for (Opinion op : painOpinions) {
			AggregateResult r = aggregates.get(op.getCustomerId());
			if (r == null) {
				String customerName = customerDAO.findById(op.getCustomerId()).getName();
				r = new AggregateResult().setCustomerId(op.getCustomerId()).setPainId(painId).setPainName(painName)
						.setCustomerName(customerName);
				aggregates.put(op.getCustomerId(), r);
			}
			r.addOpinion(op);
		}

		for (AggregateResult ar : aggregates.values())
			ar.calculateTotal();

		List<AggregateResult> results = new ArrayList<AggregateResult>(aggregates.values());
		Collections.sort(results, AggregateResult.AverageDescendingComparator);
		return results;
	}
}
