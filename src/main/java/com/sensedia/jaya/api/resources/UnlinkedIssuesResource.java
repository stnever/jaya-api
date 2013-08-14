package com.sensedia.jaya.api.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensedia.jaya.api.JayaConfiguration.JiraConfiguration;
import com.sensedia.jaya.api.access.RequestUser;
import com.sensedia.jaya.api.model.User;
import com.sensedia.jaya.api.utils.Base64;
import com.sensedia.jaya.api.utils.Input;
import com.sensedia.jaya.api.utils.Utils;

@Path("/unlinked-issues")
@Produces("application/json")
public class UnlinkedIssuesResource {

	private HttpClient httpClient;
	private JiraConfiguration jiraConfiguration;
	private static Logger _logger = LoggerFactory.getLogger(UnlinkedIssuesResource.class.getName());

	public UnlinkedIssuesResource(HttpClient httpClient, JiraConfiguration jiraConfiguration) {
		super();
		this.httpClient = httpClient;
		this.jiraConfiguration = jiraConfiguration;
	}

	@GET
	public List<Object> getUnlinkedIssues(@RequestUser User u) {
		_logger.info("Retrieving all unlinked issues");
		JsonNode json = get(
				"/search",
				"maxResults",
				"200",
				"fields",
				"issuelinks,summary",
				"jql",
				"project = AG and status not in ( Cancelado, Fechado, Rejeitado, 'Entrega Parcial' ) and issuetype in ( Épico, Estória )");
		List<Object> result = new ArrayList<Object>();

		for (Iterator<JsonNode> it = json.findValue("issues").elements(); it.hasNext();) {
			JsonNode jsonIssue = it.next();

			String key = jsonIssue.get("key").textValue();
			String summary = jsonIssue.get("fields").get("summary").textValue();

			List<Map<String,Object>> links = new ArrayList<Map<String,Object>>();
			for (Iterator<JsonNode> jit = jsonIssue.get("fields").findValue("issuelinks").elements(); jit.hasNext();) {
				JsonNode jsonLink = jit.next();

				JsonNode inwardIssue = jsonLink.get("inwardIssue");
				JsonNode outwardIssue = jsonLink.get("outwardIssue");
				JsonNode otherIssue = (inwardIssue != null) ? inwardIssue : outwardIssue;
				if (otherIssue != null)
					links.add(Utils.makeMap("key", otherIssue.get("key").textValue()));
			}
			
			Collections.sort(links, new Comparator<Map<String,Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return ((String)o1.get("key")).compareTo((String)o2.get("key"));
				}
			});

			Map<String, Object> issue = Utils.makeMap("key", key, "summary", summary, "links", links);
			result.add(issue);
		}

		return result;

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
