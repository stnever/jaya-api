package com.sensedia.jaya.api.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensedia.jaya.api.JayaConfiguration.JiraConfiguration;
import com.sensedia.jaya.api.model.Pain;
import com.sensedia.jaya.api.utils.Base64;
import com.sensedia.jaya.api.utils.Input;

public class PainDAO {

	private static Logger _logger = LoggerFactory.getLogger(PainDAO.class.getName());

	private JiraConfiguration jiraConfiguration;
	private HttpClient httpClient;

	public PainDAO(JiraConfiguration jiraConfiguration, HttpClient httpClient) {
		super();
		this.jiraConfiguration = jiraConfiguration;
		this.httpClient = httpClient;
	}

	public List<Pain> findAll() {
		_logger.info("Retrieving all pains");
		JsonNode json = get("/search", "maxResults", "100", "fields", "summary,description", "jql", "project=PG");
		List<Pain> result = new ArrayList<Pain>();

		for (Iterator<JsonNode> it = json.findValue("issues").elements(); it.hasNext();) {
			JsonNode jsonIssue = it.next();
			Pain pain = new Pain().setId(jsonIssue.get("key").textValue());

			pain.setTitle(jsonIssue.get("fields").get("summary").textValue());
			pain.setDescription(jsonIssue.get("fields").get("description").textValue());
			result.add(pain);
		}

		return result;
	}

	public Pain findById(String id) {
		JsonNode jsonIssue = get("/issue/" + id);
		Pain pain = new Pain().setId(jsonIssue.get("key").textValue());

		pain.setTitle(jsonIssue.get("fields").get("summary").textValue());
		pain.setDescription(jsonIssue.get("fields").get("description").textValue());

		return pain;
	}

	private JsonNode get(String resource, String... params) {
		try {
			URIBuilder builder = new URIBuilder(jiraConfiguration.getJiraApiRoot() + resource);
			for (int i = 0; i < params.length; i += 2) {
				builder.setParameter(params[i], params[i + 1]);
			}

			HttpGet get = new HttpGet(builder.build());

			// set up authorization header
			String base64 = Base64.encodeLines((jiraConfiguration.getJiraUser() + ":" + jiraConfiguration
					.getJiraPassword()).getBytes());
			get.setHeader("Authorization", "Basic " + base64);

			HttpResponse resp = httpClient.execute(get);
			String json = Input.stream(resp.getEntity().getContent()).readString();
			return new ObjectMapper().readTree(json);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
