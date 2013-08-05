package com.sensedia.jaya.api.resources;

import java.util.List;

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
import com.sensedia.jaya.api.model.Comment;
import com.sensedia.jaya.api.model.Customer;
import com.sensedia.jaya.api.model.Opinion;
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

	public CustomersResource(CustomerDAO customerDAO, CustomerCommentDAO commentDAO, OpinionDAO opinionDAO) {
		super();
		this.customerDAO = customerDAO;
		this.commentDAO = commentDAO;
		this.opinionDAO = opinionDAO;
	}

	private static Logger _logger = LoggerFactory.getLogger(CustomersResource.class.getName());

	@GET
	public List<Customer> findAll(@RequestUser User u) {
		return customerDAO.findAll();
	}

	@POST
	@Consumes("application/json")
	public Response create(@RequestUser User u, Customer customer) {
		customer.setId(null);
		Long id = customerDAO.insert(customer);
		return Response.status(Response.Status.CREATED).header("Location", id).build();
	}

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Find customer by ID", notes = "Add extra notes here", responseClass = "com.sensedia.jaya.api.model.Customer")
	public Customer findById(@RequestUser User u,
			@ApiParam(value = "ID of Pain to fetch", required = true) @PathParam("id") Long id) {
		_logger.info("Retrieving Pain #{}", id);

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

	@PUT
	@Path("/{id}")
	@Consumes("application/json")
	public Response update(@RequestUser User u, Customer customer) {
		customerDAO.update(customer);
		return Response.status(Response.Status.ACCEPTED).build();
	}

}
