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
	private DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

	@Valid
	@NotNull
	@JsonProperty
	private JiraConfiguration jiraConfiguration = new JiraConfiguration();

	@Valid
	@NotNull
	@JsonProperty
	private HttpClientConfiguration httpClientConfiguration = new HttpClientConfiguration();

	public DatabaseConfiguration getDatabaseConfiguration() {
		return databaseConfiguration;
	}

	public JiraConfiguration getJiraConfiguration() {
		return jiraConfiguration;
	}

	public HttpClientConfiguration getHttpClientConfiguration() {
		return httpClientConfiguration;
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

		public String getJiraUser() {
			return jiraUser;
		}

		public String getJiraPassword() {
			return jiraPassword;
		}

	}
}
