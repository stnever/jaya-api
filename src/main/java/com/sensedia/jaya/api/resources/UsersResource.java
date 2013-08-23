package com.sensedia.jaya.api.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.sensedia.jaya.api.access.RequestUser;
import com.sensedia.jaya.api.dao.UserDAO;
import com.sensedia.jaya.api.model.User;
import com.sensedia.jaya.api.utils.Utils;

@Path("/users")
public class UsersResource {

	UserDAO dao = null;

	public UsersResource(UserDAO dao) {
		this.dao = dao;
	}

	@GET
	public List<Map<String, Object>> listAllUsers(@RequestUser User currentUser) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (User u : dao.findAll()) {
			result.add(Utils.makeMap("name", u.getName()));
		}
		return result;
	}

}
