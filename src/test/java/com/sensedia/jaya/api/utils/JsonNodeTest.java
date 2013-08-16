package com.sensedia.jaya.api.utils;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.TestCase;

public class JsonNodeTest extends TestCase {

	public void testJsonNode() throws Exception {
		String s = "{\"user_id\": \"123456\", \"fields\": { \"summary\": \"blabla\"}, \"issues\": [ {\"key\":1}, {\"key\":2}]}";
		JsonNode n = new ObjectMapper().readTree(s);

		System.out.println(n);
		assertEquals("123456", n.get("user_id").textValue());
		assertNull(n.get("missing_field"));

		assertEquals("blabla", n.get("fields").get("summary").asText());
		assertEquals("blabla", n.path("fields").path("summary").textValue());
		assertEquals(null, n.path("image").path("url").textValue());


		assertTrue(n.has("issues"));
		System.out.println(n.findValue("issues").elements());

		for (Iterator<JsonNode> it = n.findValue("issues").elements(); it.hasNext();) {
			JsonNode issue = it.next();
			System.out.println(issue.get("key"));
		}



	}
}
