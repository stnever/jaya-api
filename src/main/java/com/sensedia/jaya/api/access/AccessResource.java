package com.sensedia.jaya.api.access;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.http.client.HttpClient;

import com.sensedia.jaya.api.dao.UserDAO;
import com.sensedia.jaya.api.model.User;

@Path("/access")
@Produces("application/json")
public class AccessResource {

	private UserDAO userDAO;

	private HttpClient httpClient;

	public AccessResource(UserDAO userDAO, HttpClient client) {
		super();
		this.userDAO = userDAO;
		this.httpClient = client;
	}

	public Response connectToGoogle(String accessToken) {
		/*
		 * 1. Usar o accessToken para fazer um request para:
		 * 
		 * https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=xxxxxx
		 * 
		 * 2. Se o retorno for inválido, retornar uma resposta apropriada.
		 * 
		 * 3. Gerar um sessionId novo.
		 * 
		 * 4. Criar um User com os dados recebidos em 1 mais o sessionId gerado
		 * em 3.
		 * 
		 * 5. Retornar o sessionId.
		 */
		return null;
	}

	public Response disconnectFromGoogle(@RequestUser User u) {
		// O processo de logout envolve apenas apagar o sessionId do user (não
		// podemos apagar o user
		// pois suas opiniões precisam ser mantidas)
		u.setSessionId(null);
		userDAO.update(u);

		return Response.status(Response.Status.OK).build();
	}

}
