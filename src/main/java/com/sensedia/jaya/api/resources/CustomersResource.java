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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sensedia.jaya.api.access.RequestUser;
import com.sensedia.jaya.api.dao.CustomerCommentDAO;
import com.sensedia.jaya.api.dao.CustomerDAO;
import com.sensedia.jaya.api.dao.OpinionDAO;
import com.sensedia.jaya.api.dao.PainDAO;
import com.sensedia.jaya.api.model.AggregateResult;
import com.sensedia.jaya.api.model.Comment;
import com.sensedia.jaya.api.model.Customer;
import com.sensedia.jaya.api.model.Opinion;
import com.sensedia.jaya.api.model.Pain;
import com.sensedia.jaya.api.model.User;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/customers")
@Produces("application/json")
@Api(value = "/customers", description = "Operations that create, update, and list customers.")
public class CustomersResource {

	private CustomerDAO customerDAO;
	private CustomerCommentDAO commentDAO;
	private OpinionDAO opinionDAO;
	private PainDAO painDAO;

	private static Logger _logger = LoggerFactory.getLogger(CustomersResource.class.getName());

	public CustomersResource(CustomerDAO customerDAO, CustomerCommentDAO commentDAO, OpinionDAO opinionDAO, PainDAO painDAO) {
		super();
		this.customerDAO = customerDAO;
		this.commentDAO = commentDAO;
		this.opinionDAO = opinionDAO;
		this.painDAO = painDAO;
	}

	@GET
	public List<Customer> findAll(@RequestUser User u) {
		_logger.info("Retrieving all customers.");
		return customerDAO.findAll();
	}

	@POST
	@Consumes("application/json")
	public Response create(@RequestUser User u, Customer customer) {
		_logger.info("Creating customer {}", customer);
		customer.setId(null);
		Long id = customerDAO.insert(customer);
		_logger.info("Customer ID: {}", id);
		return Response.status(Response.Status.CREATED).header("Location", id.toString()).build();
	}

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Find customer by ID", notes = "Add extra notes here", responseClass = "com.sensedia.jaya.api.model.Customer")
	public Customer findById(@RequestUser User u,
			@ApiParam(value = "ID of Pain to fetch", required = true) @PathParam("id") Long id) {
		_logger.info("Retrieving customer {}", id);

		Customer c = customerDAO.findById(id);
		if (c == null)
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build());

		List<Comment> comments = commentDAO.findByCustomer(id);
		if (comments != null)
			c.setComments(comments);

		List<Opinion> opinions = opinionDAO.findByCustomer(id);
		if (opinions != null)
			c.setOpinions(opinions);

		return c;
	}

	@POST
	@Path("/{id}/comments")
	public Response addComment(@RequestUser User u, @PathParam("id") String customerId, String comment) {
		_logger.info("Adding comment to customer {} by user {}: {}", customerId, u, comment);
		Comment c = new Comment().setDate(new Date()).setText(comment).setUserId(u.getUserId());
		commentDAO.insertComment(customerId, c);
		return Response.status(Response.Status.OK).build();
	}

	@PUT
	@Path("/{customerId}/opinions/{painId}")
	public Response addOpinion(@RequestUser User u, @PathParam("painId") String painId,
			@PathParam("customerId") Long customerId, Map<String, String> data ) {
		Integer value = Integer.valueOf(data.get("value"));
		String comment = data.get("comment");
		_logger.info("Adding opinion to pain {}, customer {}, by user {}: {} {}", painId, customerId, u, value, comment);

		Opinion o = opinionDAO.findByKey(painId, customerId, u.getUserId());
		_logger.info("Retrieved opinion: {}", o);
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

	@PUT
	@Path("/{id}")
	@Consumes("application/json")
	public Response update(@RequestUser User u, Customer customer) {
		_logger.info("Updating customer {}", customer);
		customerDAO.update(customer);
		return Response.status(Response.Status.ACCEPTED).build();
	}

	@GET
	@Path("{customerId}/results")
	public List<AggregateResult> calculateAggregateResults(@PathParam("customerId") Long customerId) {
		Map<String, AggregateResult> aggregates = new HashMap<String, AggregateResult>();

		String customerName = customerDAO.findById(customerId).getName();
		
		Map<String, String> painNames = new HashMap<String, String>();
		for ( Pain p : painDAO.findAll() )
			painNames.put(p.getId(), p.getTitle());

		List<Opinion> painOpinions = opinionDAO.findByCustomer(customerId);
		for (Opinion op : painOpinions) {
			AggregateResult r = aggregates.get(op.getPainId());
			if (r == null) {
				String painName = painNames.get(op.getPainId());
				r = new AggregateResult().setCustomerId(op.getCustomerId()).setPainId(op.getPainId()).setPainName(painName)
						.setCustomerName(customerName).setCustomerId(op.getCustomerId());
				aggregates.put(op.getPainId(), r);
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
