package com.sensedia.jaya.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sensedia.jaya.api.dao.PainDAO;
import com.sensedia.jaya.api.model.Pain;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/pains")
@Produces("application/json")
@Api(value = "/pains", description = "Operations that create, update, and list Pains.")
public class PainResource {

	private PainDAO painDao;

	public PainResource(PainDAO painDao) {
		super();
		this.painDao = painDao;
	}

	private static Logger _logger = LoggerFactory.getLogger(PainResource.class.getName());

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Find pain by ID", notes = "Add extra notes here", responseClass="com.sensedia.jaya.api.Pain")
	public Pain findById(
			@ApiParam(value = "ID of Pain to fetch", required = true)
			@PathParam("id") String id) {
		_logger.info("Retrieving Pain #{}", id);
		return new Pain().setId(id).setTitle(painDao.findNameById(Integer.valueOf(id)));
	}

}
