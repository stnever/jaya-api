package com.sensedia.jaya.api.resources;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sensedia.jaya.api.JayaConfiguration;

public class UnlinkedIssuesResourceTest extends TestCase {

	private UnlinkedIssuesResource resource = null;

	@Override
	protected void setUp() throws Exception {
		JayaConfiguration config = new ObjectMapper(new YAMLFactory()).readValue(new File("./testing.yaml"),
				JayaConfiguration.class);
		resource = new UnlinkedIssuesResource(new DefaultHttpClient(), config.getJiraConfiguration());
	}

	public void testFindUnlinkedIssues() throws Exception {
		List<Object> issues = resource.getUnlinkedIssues(null);
		assertNotNull(issues);
		assertTrue(issues.size() > 0);

		for (Object o : issues)
			System.out.println(o);
	}

}
