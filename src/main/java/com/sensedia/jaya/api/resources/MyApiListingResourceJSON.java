package com.sensedia.jaya.api.resources;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;

@Path("/api-docs")
@Api("/api-docs")
@Produces({"application/json"})
public class MyApiListingResourceJSON extends ApiListingResourceJSON {}