package com.sensedia.jaya.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.client.HttpClientConfiguration;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.db.DatabaseConfiguration;

public class JayaConfiguration extends Configuration {

	@Valid
	@NotNull
	@JsonProperty
	private DatabaseConfiguration database = new DatabaseConfiguration();

	@Valid
	@NotNull
	@JsonProperty
	private JiraConfiguration jira = new JiraConfiguration();

	@Valid
	@NotNull
	@JsonProperty
	private HttpClientConfiguration httpClient = new HttpClientConfiguration();

	@Valid
	@NotNull
	@JsonProperty
	private GoogleConfiguration googleApp = new GoogleConfiguration();

	public DatabaseConfiguration getDatabaseConfiguration() {
		return database;
	}

	public JiraConfiguration getJiraConfiguration() {
		return jira;
	}

	public HttpClientConfiguration getHttpClientConfiguration() {
		return httpClient;
	}

	public GoogleConfiguration getGoogleConfiguration() {
		return googleApp;
	}

	public static class JiraConfiguration {

		@Valid
		@NotNull
		@JsonProperty
		private String jiraUser;

		@Valid
		@NotNull
		@JsonProperty
		private String jiraPassword;

		@Valid
		@NotNull
		@JsonProperty
		private String jiraUrl;

		public String getJiraUser() {
			return jiraUser;
		}

		public String getJiraPassword() {
			return jiraPassword;
		}

		public String getJiraUrl() {
			return jiraUrl;
		}

		public String getJiraApiRoot() {
			return jiraUrl + "/rest/api/2";
		}

	}

	public static class GoogleConfiguration {
		@Valid
		@NotNull
		@JsonProperty
		private String clientId;

		@Valid
		@JsonProperty
		private String restrictDomain;

		public String getClientId() {
			return clientId;
		}

		public String getRestrictDomain() {
			return restrictDomain;
		}
	}
}
